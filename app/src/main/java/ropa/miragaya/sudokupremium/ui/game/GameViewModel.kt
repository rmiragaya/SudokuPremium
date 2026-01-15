package ropa.miragaya.sudokupremium.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ropa.miragaya.sudokupremium.data.SAMPLE_PUZZLE
import ropa.miragaya.sudokupremium.domain.factory.BoardFactory

class GameViewModel : ViewModel() {

    // 1. Tablero inicial. // todo que venga del repositorio
    private val initialBoard = BoardFactory.fromString(SAMPLE_PUZZLE)

    // 2. El Estado Mutable (Privado)
    private val _uiState = MutableStateFlow(GameState(board = initialBoard))

    // 3. El Estado Inmutable (Público para la UI)
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun onCellClicked(cellId: Int) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedCellId == cellId) null else cellId
            currentState.copy(selectedCellId = newSelection)
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

            // Validar que no sea una pista inicial
            val cell = currentBoard.cells.first { it.id == currentSelectedId }
            if (cell.isGiven) return@update currentState

            val newBoard = currentBoard
                .withCellCleared(currentSelectedId)
                .validateConflicts()

            currentState.copy(board = newBoard)
        }
    }
}