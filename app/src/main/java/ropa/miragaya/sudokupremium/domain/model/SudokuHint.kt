package ropa.miragaya.sudokupremium.domain.model

data class SudokuHint(
    val strategyName: String,
    val description: String,
    val row: Int,
    val col: Int,
    val value: Int?,
    val notesRemoved: List<Int> = emptyList()
)