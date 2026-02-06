package ropa.miragaya.sudokupremium.ui.game

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuHint

data class GameUiState(
    val board: Board,
    val solvedBoard: Board? = null,
    val elapsedTimeSeconds: Long = 0,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val selectedCellId: Int? = null,
    val highlightedCellIds: Set<Int> = emptySet(),
    val sameValueCellIds: Set<Int> = emptySet(),
    val isNoteMode: Boolean = false,
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val activeHints: List<SudokuHint> = emptyList(),
    val currentHintIndex: Int = 0,
    val showNoHintFound: Boolean = false,
    val showMistakeError: Boolean = false,
    val mistakeCount: Int = 0,
    val completedNumbers: Set<Int> = emptySet(),
) {
    val activeHint: SudokuHint?
        get() = if (activeHints.isNotEmpty()) activeHints[currentHintIndex] else null
}