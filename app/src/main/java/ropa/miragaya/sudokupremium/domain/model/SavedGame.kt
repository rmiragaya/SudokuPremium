package ropa.miragaya.sudokupremium.domain.model

data class SavedGame(
    val board: Board,
    val solvedBoard: Board,
    val elapsedTimeSeconds: Long,
    val difficulty: Difficulty,
    val hintsUsed: Int = 0,
    val rewardedHintsAvailable: Int = 0
)
