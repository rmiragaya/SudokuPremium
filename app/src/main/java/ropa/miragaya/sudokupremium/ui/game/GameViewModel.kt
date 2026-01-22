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

        private val history = ArrayDeque<Board>()

        init {
            initializeGame()
        }

        private fun initializeGame() {
            viewModelScope.launch {
                val savedGame = repository.getSavedGame().firstOrNull()

                if (savedGame != null) {
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

                resumeTimer()
            }
        }


        fun onCellClicked(cellId: Int) {
            _uiState.update { currentState ->
                val newSelection = if (currentState.selectedCellId == cellId) null else cellId

                val selectedCell = if (newSelection != null) currentState.board.cells.find { it.id == newSelection } else null

                val highlights = if (selectedCell != null) {
                    currentState.board.getPeers(selectedCell.id)
                } else {
                    emptySet()
                }

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
            val currentSelectedId = _uiState.value.selectedCellId ?: return // sin selección no hay paraíso

            var justWon = false

            _uiState.update { currentState ->

                val newBoard = currentState.board.playMove(
                    cellId = currentSelectedId,
                    number = number,
                    isNoteMode = currentState.isNoteMode
                )

                if (newBoard == currentState.board) {
                    return@update currentState
                }

                saveToHistory()

                justWon = newBoard.isSolved()

                currentState.copy(
                    board = newBoard,
                    isComplete = justWon
                )
            }

            if (justWon) handleVictory() else saveGame()
        }

        private fun handleVictory() {
            pauseTimer()

            val finalTime = _uiState.value.elapsedTimeSeconds
            val difficulty = _uiState.value.difficulty

            viewModelScope.launch {
                repository.saveVictory(finalTime, difficulty)
            }
        }

        fun startNewGame() {
            viewModelScope.launch {
                history.clear()

                val newBoard = BoardFactory.fromString(SAMPLE_PUZZLE)


                _uiState.update {
                    it.copy(
                        board = newBoard,
                        elapsedTimeSeconds = 0,
                        isComplete = false,
                        isNoteMode = false,
                        selectedCellId = null,
                        highlightedCellIds = emptySet(),
                        sameValueCellIds = emptySet()
                    )
                }

                saveGame()
                resumeTimer()
            }
        }
        fun onDeleteInput() {
            val currentSelectedId = _uiState.value.selectedCellId ?: return

            _uiState.update { currentState ->
                val currentBoard = currentState.board
                val cell = currentBoard.cells.first { it.id == currentSelectedId }

                if (cell.isGiven) return@update currentState

                if (cell.value == null && cell.notes.isEmpty()) {
                    return@update currentState
                }

                saveToHistory()

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
                // para no guardar tableros vacíos
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

        private fun saveToHistory() {
            // solo guardo los ultimos 50 movimientos
            if (history.size >= 50) {
                history.removeFirst()
            }
            history.addLast(_uiState.value.board)
        }

        fun onUndo() {
            if (history.isEmpty()) return

            val previousBoard = history.removeLast()
            _uiState.update {
                it.copy(board = previousBoard)
            }
            saveGame()
        }

        override fun onCleared() {
            super.onCleared()
            saveGame()
            timerJob?.cancel()
        }
    }