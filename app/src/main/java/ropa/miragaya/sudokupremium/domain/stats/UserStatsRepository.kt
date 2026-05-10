package ropa.miragaya.sudokupremium.domain.stats

import ropa.miragaya.sudokupremium.domain.model.Difficulty

interface UserStatsRepository {
    fun trackGameStarted(difficulty: Difficulty)

    fun trackGameCompleted(difficulty: Difficulty, elapsedSeconds: Long, hintsUsed: Int, mistakesRevealed: Int)
}
