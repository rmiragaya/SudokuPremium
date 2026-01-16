package ropa.miragaya.sudokupremium.ui.game

import ropa.miragaya.sudokupremium.domain.model.Board

data class GameUiState(
    val board: Board,
    val selectedCellId: Int? = null,
    val highlightedCellIds: Set<Int> = emptySet(), // cross-highlighting
    val sameValueCellIds: Set<Int> = emptySet(),
    val isNoteMode: Boolean = false,               // lápiz
    val isComplete: Boolean = false
)