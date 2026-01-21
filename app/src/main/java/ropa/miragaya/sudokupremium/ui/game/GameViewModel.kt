package ropa.miragaya.sudokupremium.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.data.SAMPLE_PUZZLE
import ropa.miragaya.sudokupremium.domain.factory.BoardFactory
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(board = Board.createEmpty()))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        initializeGame()
    }

    private fun initializeGame() {
        viewModelScope.launch {
            // 2. CARGA DE DATOS: Preguntamos al repo si hay algo guardado
            val savedGame = repository.getSavedGame().firstOrNull()

            if (savedGame != null) {
                // CASO A: Restauramos partida
                _uiState.update {
                    it.copy(
                        board = savedGame.board,
                        elapsedTimeSeconds = savedGame.elapsedTimeSeconds,
                        difficulty = savedGame.difficulty
                    )
                }
            } else {
                // todo que llegue de un repo un sudoku
                val initialBoard = BoardFactory.fromString(SAMPLE_PUZZLE)
                _uiState.update {
                    it.copy(board = initialBoard, elapsedTimeSeconds = 0)
                }
                saveGame()
            }

            // 3. Arrancamos el reloj
            resumeTimer()
        }
    }


    fun onCellClicked(cellId: Int) {
        _uiState.update { currentState ->
            // Lógica de toggle selección (si toco la misma, deselecciono)
            val newSelection = if (currentState.selectedCellId == cellId) null else cellId

            // Lógica de Highlight (Solo si hay selección y la celda está vacía)
            val selectedCell = if (newSelection != null) currentState.board.cells.find { it.id == newSelection } else null

            // 1. Peers (Tenue): Siempre que haya selección
            val highlights = if (selectedCell != null) {
                currentState.board.getPeers(selectedCell.id)
            } else {
                emptySet()
            }

            // 2. Same Value (Fuerte): Solo si la celda tiene valor
            val sameValues = if (selectedCell != null && selectedCell.value != null) {
                currentState.board.getCellsWithValue(selectedCell.value) - selectedCell.id
            } else {
                emptySet()
            }

            currentState.copy(
                selectedCellId = newSelection,
                highlightedCellIds = highlights,
                sameValueCellIds = sameValues
            )
        }
    }

    fun toggleNoteMode() {
        _uiState.update { it.copy(isNoteMode = !it.isNoteMode) }
    }

    fun onNumberInput(number: Int) {
        val currentSelectedId = _uiState.value.selectedCellId ?: return // no selection, no input

        _uiState.update { currentState ->
            val currentBoard = currentState.board
            val cell = currentBoard.cells.first { it.id == currentSelectedId }

            if (cell.isGiven) return@update currentState

            val newBoard = if (currentState.isNoteMode) {
                if (cell.value == null) {
                    currentBoard.withNoteToggle(currentSelectedId, number)
                } else {
                    currentBoard
                }
            } else {
                currentBoard
                    .withCellValue(currentSelectedId, number)
                    .validateConflicts()
            }

            currentState.copy(board = newBoard)
        }

        saveGame()
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

        saveGame()
    }

    fun resumeTimer() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _uiState.update { it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1) }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun saveGame() {
        viewModelScope.launch {
            val currentState = _uiState.value
            // Validación de seguridad para no guardar tableros vacíos
            if (currentState.board.cells.isNotEmpty()) {
                repository.saveGame(
                    SavedGame(
                        board = currentState.board,
                        elapsedTimeSeconds = currentState.elapsedTimeSeconds,
                        difficulty = currentState.difficulty
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveGame()
        timerJob?.cancel()
    }
}