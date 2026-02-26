package ropa.miragaya.sudokupremium.domain.model

data class SudokuHint(
    val strategyName: String,
    val description: String,
    val targetCellIndex: Int? = null,
    val valueToSet: Int? = null,
    val notesToRemove: Map<Int, List<Int>> = emptyMap(),
    val highlightCells: List<Int> = emptyList(),
    val stepBoard: Board
)