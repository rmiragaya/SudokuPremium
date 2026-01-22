package ropa.miragaya.sudokupremium.ui.game

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

data class GameUiState(
    val board: Board,
    val elapsedTimeSeconds: Long = 0,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val selectedCellId: Int? = null,
    val highlightedCellIds: Set<Int> = emptySet(),
    val sameValueCellIds: Set<Int> = emptySet(),
    val isNoteMode: Boolean = false,
    val isComplete: Boolean = false
)