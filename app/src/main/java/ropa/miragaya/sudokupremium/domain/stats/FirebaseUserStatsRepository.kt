package ropa.miragaya.sudokupremium.domain.stats

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.auth.AuthSessionManager
import ropa.miragaya.sudokupremium.crash.CrashReporter
import ropa.miragaya.sudokupremium.domain.model.Difficulty

@Singleton
class FirebaseUserStatsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authSessionManager: AuthSessionManager,
    private val crashReporter: CrashReporter
) : UserStatsRepository {

    override fun trackGameStarted(difficulty: Difficulty) {
        val uid = authSessionManager.currentUserId()
        if (uid == null) {
            crashReporter.log("Skipped gameStarted stats: missing Firebase uid")
            return
        }

        val userDocument = firestore.collection(COLLECTION_USERS).document(uid)
        val summaryDocument = userDocument.collection(COLLECTION_STATS).document(DOCUMENT_SUMMARY)
        val difficultyDocument = userDocument
            .collection(COLLECTION_STATS)
            .document(DOCUMENT_BY_DIFFICULTY)
            .collection(COLLECTION_ITEMS)
            .document(difficulty.statsKey())

        firestore.runBatch { batch ->
            batch.set(
                userDocument,
                mapOf(
                    FIELD_LAST_SEEN_AT to FieldValue.serverTimestamp(),
                    FIELD_IS_ANONYMOUS to true
                ),
                SetOptions.merge()
            )
            batch.set(summaryDocument, startedStatsUpdate(), SetOptions.merge())
            batch.set(difficultyDocument, startedStatsUpdate(), SetOptions.merge())
        }.addOnFailureListener { throwable ->
            crashReporter.recordNonFatal(throwable)
        }
    }

    override fun trackGameCompleted(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        mistakesRevealed: Int
    ) {
        val uid = authSessionManager.currentUserId()
        if (uid == null) {
            crashReporter.log("Skipped gameCompleted stats: missing Firebase uid")
            return
        }

        val userDocument = firestore.collection(COLLECTION_USERS).document(uid)
        val summaryDocument = userDocument.collection(COLLECTION_STATS).document(DOCUMENT_SUMMARY)
        val difficultyDocument = userDocument
            .collection(COLLECTION_STATS)
            .document(DOCUMENT_BY_DIFFICULTY)
            .collection(COLLECTION_ITEMS)
            .document(difficulty.statsKey())
        val completedGameDocument = userDocument.collection(COLLECTION_COMPLETED_GAMES).document()

        firestore.runBatch { batch ->
            val completedUpdate = completedStatsUpdate(
                difficulty = difficulty,
                elapsedSeconds = elapsedSeconds,
                hintsUsed = hintsUsed,
                mistakesRevealed = mistakesRevealed
            )

            batch.set(
                userDocument,
                mapOf(FIELD_LAST_SEEN_AT to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            batch.set(summaryDocument, completedUpdate, SetOptions.merge())
            batch.set(difficultyDocument, completedUpdate, SetOptions.merge())
            batch.set(
                completedGameDocument,
                mapOf(
                    FIELD_DIFFICULTY to difficulty.statsKey(),
                    FIELD_ELAPSED_SECONDS to elapsedSeconds,
                    FIELD_HINTS_USED to hintsUsed,
                    FIELD_MISTAKES_REVEALED to mistakesRevealed,
                    FIELD_COMPLETED_AT to FieldValue.serverTimestamp()
                )
            )
        }.addOnFailureListener { throwable ->
            crashReporter.recordNonFatal(throwable)
        }
    }

    override fun trackHintLimitReached() {
        trackMonetizationEvent(
            countField = FIELD_HINT_LIMIT_REACHED,
            timestampField = FIELD_LAST_HINT_LIMIT_REACHED_AT
        )
    }

    override fun trackRewardedHintAdRequested() {
        trackMonetizationEvent(
            countField = FIELD_REWARDED_HINT_ADS_REQUESTED,
            timestampField = FIELD_LAST_REWARDED_HINT_AD_REQUESTED_AT
        )
    }

    override fun trackRewardedHintAdEarned() {
        trackMonetizationEvent(
            countField = FIELD_REWARDED_HINT_ADS_EARNED,
            timestampField = FIELD_LAST_REWARDED_HINT_AD_EARNED_AT
        )
    }

    override fun trackRewardedHintAdDismissed() {
        trackMonetizationEvent(
            countField = FIELD_REWARDED_HINT_ADS_DISMISSED,
            timestampField = FIELD_LAST_REWARDED_HINT_AD_DISMISSED_AT
        )
    }

    override fun trackRewardedHintAdFailed(reason: String?) {
        val safeReason = reason
            ?.take(MAX_REASON_LENGTH)
            ?: VALUE_UNKNOWN

        trackMonetizationEvent(
            countField = FIELD_REWARDED_HINT_ADS_FAILED,
            timestampField = FIELD_LAST_REWARDED_HINT_AD_FAILED_AT,
            extraFields = mapOf(FIELD_LAST_REWARDED_HINT_AD_FAILURE_REASON to safeReason)
        )
    }

    override fun trackPremiumPurchaseStarted() {
        trackMonetizationEvent(
            countField = FIELD_PREMIUM_PURCHASES_STARTED,
            timestampField = FIELD_LAST_PREMIUM_PURCHASE_STARTED_AT
        )
    }

    override fun trackPremiumPurchased() {
        trackPremiumEntitlement(
            countField = FIELD_PREMIUM_PURCHASES_COMPLETED,
            timestampField = FIELD_LAST_PREMIUM_PURCHASED_AT
        )
    }

    override fun trackPremiumRestored() {
        trackPremiumEntitlement(timestampField = FIELD_LAST_PREMIUM_RESTORED_AT)
    }

    private fun trackMonetizationEvent(
        countField: String,
        timestampField: String,
        extraFields: Map<String, Any> = emptyMap()
    ) {
        val uid = authSessionManager.currentUserId()
        if (uid == null) {
            crashReporter.log("Skipped monetization stats: missing Firebase uid")
            return
        }

        val userDocument = firestore.collection(COLLECTION_USERS).document(uid)
        val monetizationDocument = userDocument.collection(COLLECTION_STATS).document(DOCUMENT_MONETIZATION)

        firestore.runBatch { batch ->
            batch.set(
                userDocument,
                mapOf(FIELD_LAST_SEEN_AT to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            batch.set(
                monetizationDocument,
                mapOf(
                    countField to FieldValue.increment(1),
                    timestampField to FieldValue.serverTimestamp(),
                    FIELD_UPDATED_AT to FieldValue.serverTimestamp()
                ) + extraFields,
                SetOptions.merge()
            )
        }.addOnFailureListener { throwable ->
            crashReporter.recordNonFatal(throwable)
        }
    }

    private fun trackPremiumEntitlement(countField: String? = null, timestampField: String) {
        val uid = authSessionManager.currentUserId()
        if (uid == null) {
            crashReporter.log("Skipped premium stats: missing Firebase uid")
            return
        }

        val userDocument = firestore.collection(COLLECTION_USERS).document(uid)
        val monetizationDocument = userDocument.collection(COLLECTION_STATS).document(DOCUMENT_MONETIZATION)
        val monetizationUpdate = buildMap {
            countField?.let { put(it, FieldValue.increment(1)) }
            put(timestampField, FieldValue.serverTimestamp())
            put(FIELD_IS_PREMIUM, true)
            put(FIELD_UPDATED_AT, FieldValue.serverTimestamp())
        }

        firestore.runBatch { batch ->
            batch.set(
                userDocument,
                mapOf(
                    FIELD_IS_PREMIUM to true,
                    FIELD_PREMIUM_UPDATED_AT to FieldValue.serverTimestamp(),
                    FIELD_LAST_SEEN_AT to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            batch.set(monetizationDocument, monetizationUpdate, SetOptions.merge())
        }.addOnFailureListener { throwable ->
            crashReporter.recordNonFatal(throwable)
        }
    }

    private fun startedStatsUpdate(): Map<String, Any> {
        return mapOf(
            FIELD_GAMES_STARTED to FieldValue.increment(1),
            FIELD_LAST_PLAYED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
    }

    private fun completedStatsUpdate(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        mistakesRevealed: Int
    ): Map<String, Any> {
        return mapOf(
            FIELD_DIFFICULTY to difficulty.statsKey(),
            FIELD_GAMES_COMPLETED to FieldValue.increment(1),
            FIELD_TOTAL_COMPLETION_TIME_SECONDS to FieldValue.increment(elapsedSeconds),
            FIELD_HINTS_USED to FieldValue.increment(hintsUsed.toLong()),
            FIELD_MISTAKES_REVEALED to FieldValue.increment(mistakesRevealed.toLong()),
            FIELD_LAST_COMPLETED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
    }

    private fun Difficulty.statsKey(): String = name.lowercase()

    private companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_STATS = "stats"
        const val COLLECTION_ITEMS = "items"
        const val COLLECTION_COMPLETED_GAMES = "completedGames"

        const val DOCUMENT_SUMMARY = "summary"
        const val DOCUMENT_BY_DIFFICULTY = "byDifficulty"
        const val DOCUMENT_MONETIZATION = "monetization"

        const val FIELD_LAST_SEEN_AT = "lastSeenAt"
        const val FIELD_IS_ANONYMOUS = "isAnonymous"
        const val FIELD_IS_PREMIUM = "isPremium"
        const val FIELD_UPDATED_AT = "updatedAt"
        const val FIELD_LAST_PLAYED_AT = "lastPlayedAt"
        const val FIELD_LAST_COMPLETED_AT = "lastCompletedAt"
        const val FIELD_COMPLETED_AT = "completedAt"
        const val FIELD_PREMIUM_UPDATED_AT = "premiumUpdatedAt"
        const val FIELD_DIFFICULTY = "difficulty"
        const val FIELD_GAMES_STARTED = "gamesStarted"
        const val FIELD_GAMES_COMPLETED = "gamesCompleted"
        const val FIELD_TOTAL_COMPLETION_TIME_SECONDS = "totalCompletionTimeSeconds"
        const val FIELD_ELAPSED_SECONDS = "elapsedSeconds"
        const val FIELD_HINTS_USED = "hintsUsed"
        const val FIELD_MISTAKES_REVEALED = "mistakesRevealed"
        const val FIELD_HINT_LIMIT_REACHED = "hintLimitReached"
        const val FIELD_REWARDED_HINT_ADS_REQUESTED = "rewardedHintAdsRequested"
        const val FIELD_REWARDED_HINT_ADS_EARNED = "rewardedHintAdsEarned"
        const val FIELD_REWARDED_HINT_ADS_DISMISSED = "rewardedHintAdsDismissed"
        const val FIELD_REWARDED_HINT_ADS_FAILED = "rewardedHintAdsFailed"
        const val FIELD_PREMIUM_PURCHASES_STARTED = "premiumPurchasesStarted"
        const val FIELD_PREMIUM_PURCHASES_COMPLETED = "premiumPurchasesCompleted"
        const val FIELD_LAST_HINT_LIMIT_REACHED_AT = "lastHintLimitReachedAt"
        const val FIELD_LAST_REWARDED_HINT_AD_REQUESTED_AT = "lastRewardedHintAdRequestedAt"
        const val FIELD_LAST_REWARDED_HINT_AD_EARNED_AT = "lastRewardedHintAdEarnedAt"
        const val FIELD_LAST_REWARDED_HINT_AD_DISMISSED_AT = "lastRewardedHintAdDismissedAt"
        const val FIELD_LAST_REWARDED_HINT_AD_FAILED_AT = "lastRewardedHintAdFailedAt"
        const val FIELD_LAST_REWARDED_HINT_AD_FAILURE_REASON = "lastRewardedHintAdFailureReason"
        const val FIELD_LAST_PREMIUM_PURCHASE_STARTED_AT = "lastPremiumPurchaseStartedAt"
        const val FIELD_LAST_PREMIUM_PURCHASED_AT = "lastPremiumPurchasedAt"
        const val FIELD_LAST_PREMIUM_RESTORED_AT = "lastPremiumRestoredAt"
        const val VALUE_UNKNOWN = "unknown"
        const val MAX_REASON_LENGTH = 80
    }
}
