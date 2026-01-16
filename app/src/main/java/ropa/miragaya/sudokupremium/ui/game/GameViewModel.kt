package ropa.miragaya.sudokupremium.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ropa.miragaya.sudokupremium.data.SAMPLE_PUZZLE
import ropa.miragaya.sudokupremium.domain.factory.BoardFactory

class GameViewModel : ViewModel() {

    // 1. Tablero inicial. // todo Volarlo. Que venga del repo
    private val initialBoard = BoardFactory.fromString(SAMPLE_PUZZLE)

    private val _uiState = MutableStateFlow(GameUiState(board = initialBoard))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onCellClicked(cellId: Int) {
        _uiState.update { currentState ->
            // Lógica de toggle selección (si toco la misma, deselecciono)
            val newSelection = if (currentState.selectedCellId == cellId) null else cellId

            // Lógica de Highlight (Solo si hay selección y la celda está vacía)
            val selectedCell = if (newSelection != null) currentState.board.cells.find { it.id == newSelection } else null

            val highlights = if (selectedCell != null && selectedCell.value == null) {
                currentState.board.getPeers(selectedCell.id)
            } else {
                emptySet()
            }

            currentState.copy(
                selectedCellId = newSelection,
                highlightedCellIds = highlights
            )
        }
    }

    fun onNumberInput(number: Int) {
        val currentSelectedId = _uiState.value.selectedCellId ?: return // no selection, no input

        _uiState.update { currentState ->
            // 1. Get actual board
            val currentBoard = currentState.board

            // 2. Check if the cell is a given
            val cell = currentBoard.cells.first { it.id == currentSelectedId }
            if (cell.isGiven) return@update currentState

            // 3. new board with the new number
            val newBoard = currentBoard
                .withCellValue(currentSelectedId, number)
                .validateConflicts()

            // 4. Emit
            currentState.copy(board = newBoard)
        }
    }

    fun onDeleteInput() {
        val currentSelectedId = _uiState.value.selectedCellId ?: return

        _uiState.update { currentState ->
            val currentBoard = currentState.board

            val cell = currentBoard.cells.first { it.id == currentSelectedId }
            if (cell.isGiven) return@update currentState

            val newBoard = currentBoard
                .withCellCleared(currentSelectedId)
                .validateConflicts()

            currentState.copy(board = newBoard)
        }
    }
}