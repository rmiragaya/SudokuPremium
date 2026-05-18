package ropa.miragaya.sudokupremium.domain.stats

import ropa.miragaya.sudokupremium.domain.model.Difficulty

interface UserStatsRepository {
    fun trackGameStarted(difficulty: Difficulty)

    fun trackGameCompleted(difficulty: Difficulty, elapsedSeconds: Long, hintsUsed: Int, mistakesRevealed: Int)

    fun trackHintLimitReached()

    fun trackRewardedHintAdRequested()

    fun trackRewardedHintAdEarned()

    fun trackRewardedHintAdDismissed()

    fun trackRewardedHintAdFailed(reason: String?)

    fun trackPremiumPurchaseStarted()

    fun trackPremiumPurchased()

    fun trackPremiumRestored()
}
