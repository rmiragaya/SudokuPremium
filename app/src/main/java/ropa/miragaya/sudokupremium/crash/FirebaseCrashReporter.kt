package ropa.miragaya.sudokupremium.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.domain.model.Difficulty

@Singleton
class FirebaseCrashReporter @Inject constructor(private val crashlytics: FirebaseCrashlytics) : CrashReporter {

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordNonFatal(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun setGameContext(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        rewardedHintsAvailable: Int,
        isPremium: Boolean,
        mistakesRevealed: Int,
        isComplete: Boolean
    ) {
        crashlytics.setCustomKey(KEY_GAME_DIFFICULTY, difficulty.name)
        crashlytics.setCustomKey(KEY_GAME_ELAPSED_SECONDS, elapsedSeconds)
        crashlytics.setCustomKey(KEY_GAME_HINTS_USED, hintsUsed)
        crashlytics.setCustomKey(KEY_GAME_REWARDED_HINTS_AVAILABLE, rewardedHintsAvailable)
        crashlytics.setCustomKey(KEY_IS_PREMIUM, isPremium)
        crashlytics.setCustomKey(KEY_GAME_MISTAKES_REVEALED, mistakesRevealed)
        crashlytics.setCustomKey(KEY_GAME_IS_COMPLETE, isComplete)
    }

    override fun clearGameContext() {
        crashlytics.setCustomKey(KEY_GAME_DIFFICULTY, VALUE_NONE)
        crashlytics.setCustomKey(KEY_GAME_ELAPSED_SECONDS, 0L)
        crashlytics.setCustomKey(KEY_GAME_HINTS_USED, 0)
        crashlytics.setCustomKey(KEY_GAME_REWARDED_HINTS_AVAILABLE, 0)
        crashlytics.setCustomKey(KEY_IS_PREMIUM, false)
        crashlytics.setCustomKey(KEY_GAME_MISTAKES_REVEALED, 0)
        crashlytics.setCustomKey(KEY_GAME_IS_COMPLETE, false)
    }

    override fun throwTestCrash() {
        check(BuildConfig.DEBUG) { "Crashlytics test crash is only available in debug builds." }
        crashlytics.log("Manual Crashlytics test crash requested from debug menu.")
        throw RuntimeException("Sudoku Premium Crashlytics test crash")
    }

    private companion object {
        const val KEY_GAME_DIFFICULTY = "game_difficulty"
        const val KEY_GAME_ELAPSED_SECONDS = "game_elapsed_seconds"
        const val KEY_GAME_HINTS_USED = "game_hints_used"
        const val KEY_GAME_REWARDED_HINTS_AVAILABLE = "rewarded_hints_available"
        const val KEY_IS_PREMIUM = "is_premium"
        const val KEY_GAME_MISTAKES_REVEALED = "game_mistakes_revealed"
        const val KEY_GAME_IS_COMPLETE = "game_is_complete"
        const val VALUE_NONE = "none"
    }
}
