package ropa.miragaya.sudokupremium.crash

import ropa.miragaya.sudokupremium.domain.model.Difficulty

interface CrashReporter {
    fun setUserId(userId: String)

    fun log(message: String)

    fun recordNonFatal(throwable: Throwable)

    fun setGameContext(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        rewardedHintsAvailable: Int,
        isPremium: Boolean,
        mistakesRevealed: Int,
        isComplete: Boolean
    )

    fun clearGameContext()

    fun throwTestCrash()
}
