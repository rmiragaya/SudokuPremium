package ropa.miragaya.sudokupremium.domain.model

data class SavedGame(
    val board: Board,
    val elapsedTimeSeconds: Long,
    val difficulty: Difficulty
)