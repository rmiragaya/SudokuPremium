package ropa.miragaya.sudokupremium.domain.model

data class SudokuPuzzle(
    val board: Board,
    val solvedBoard: Board,
    val difficulty: Difficulty
)