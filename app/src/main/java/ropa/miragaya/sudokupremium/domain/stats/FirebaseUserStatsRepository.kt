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

        const val FIELD_LAST_SEEN_AT = "lastSeenAt"
        const val FIELD_IS_ANONYMOUS = "isAnonymous"
        const val FIELD_UPDATED_AT = "updatedAt"
        const val FIELD_LAST_PLAYED_AT = "lastPlayedAt"
        const val FIELD_LAST_COMPLETED_AT = "lastCompletedAt"
        const val FIELD_COMPLETED_AT = "completedAt"
        const val FIELD_DIFFICULTY = "difficulty"
        const val FIELD_GAMES_STARTED = "gamesStarted"
        const val FIELD_GAMES_COMPLETED = "gamesCompleted"
        const val FIELD_TOTAL_COMPLETION_TIME_SECONDS = "totalCompletionTimeSeconds"
        const val FIELD_ELAPSED_SECONDS = "elapsedSeconds"
        const val FIELD_HINTS_USED = "hintsUsed"
        const val FIELD_MISTAKES_REVEALED = "mistakesRevealed"
    }
}
