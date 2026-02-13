package ropa.miragaya.sudokupremium.ui.game

import ropa.miragaya.sudokupremium.BuildConfig
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.hints.HintGenerator
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import javax.inject.Inject

private const val DEBUG_BOARD_JSON = "{\"cells\":[{\"box\":0,\"col\":0,\"id\":0,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0},{\"box\":0,\"col\":1,\"id\":1,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0},{\"box\":0,\"col\":2,\"id\":2,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":0,\"value\":6},{\"box\":1,\"col\":3,\"id\":3,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0},{\"box\":1,\"col\":4,\"id\":4,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0,\"value\":7},{\"box\":1,\"col\":5,\"id\":5,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0},{\"box\":2,\"col\":6,\"id\":6,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":0,\"value\":2},{\"box\":2,\"col\":7,\"id\":7,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0,\"value\":1},{\"box\":2,\"col\":8,\"id\":8,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":0,\"value\":8},{\"box\":0,\"col\":0,\"id\":9,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1},{\"box\":0,\"col\":1,\"id\":10,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":1,\"value\":8},{\"box\":0,\"col\":2,\"id\":11,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1},{\"box\":1,\"col\":3,\"id\":12,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":1,\"value\":2},{\"box\":1,\"col\":4,\"id\":13,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":1,\"value\":1},{\"box\":1,\"col\":5,\"id\":14,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1},{\"box\":2,\"col\":6,\"id\":15,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1,\"value\":9},{\"box\":2,\"col\":7,\"id\":16,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1,\"value\":6},{\"box\":2,\"col\":8,\"id\":17,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":1,\"value\":5},{\"box\":0,\"col\":0,\"id\":18,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":2},{\"box\":0,\"col\":1,\"id\":19,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":2,\"value\":2},{\"box\":0,\"col\":2,\"id\":20,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":2},{\"box\":1,\"col\":3,\"id\":21,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":2,\"value\":6},{\"box\":1,\"col\":4,\"id\":22,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":2},{\"box\":1,\"col\":5,\"id\":23,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":2,\"value\":8},{\"box\":2,\"col\":6,\"id\":24,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":2,\"value\":4},{\"box\":2,\"col\":7,\"id\":25,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":2,\"value\":7},{\"box\":2,\"col\":8,\"id\":26,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":2,\"value\":3},{\"box\":3,\"col\":0,\"id\":27,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3,\"value\":2},{\"box\":3,\"col\":1,\"id\":28,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3},{\"box\":3,\"col\":2,\"id\":29,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3,\"value\":5},{\"box\":4,\"col\":3,\"id\":30,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":3,\"value\":7},{\"box\":4,\"col\":4,\"id\":31,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":3,\"value\":6},{\"box\":4,\"col\":5,\"id\":32,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3,\"value\":9},{\"box\":5,\"col\":6,\"id\":33,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3},{\"box\":5,\"col\":7,\"id\":34,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":3,\"value\":8},{\"box\":5,\"col\":8,\"id\":35,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":3,\"value\":4},{\"box\":3,\"col\":0,\"id\":36,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":3,\"col\":1,\"id\":37,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":3,\"col\":2,\"id\":38,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":4,\"col\":3,\"id\":39,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":4,\"col\":4,\"id\":40,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":4,\"value\":3},{\"box\":4,\"col\":5,\"id\":41,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":5,\"col\":6,\"id\":42,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":5,\"col\":7,\"id\":43,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":4},{\"box\":5,\"col\":8,\"id\":44,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":4,\"value\":9},{\"box\":3,\"col\":0,\"id\":45,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":3,\"col\":1,\"id\":46,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":5,\"value\":9},{\"box\":3,\"col\":2,\"id\":47,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":4,\"col\":3,\"id\":48,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":4,\"col\":4,\"id\":49,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":4,\"col\":5,\"id\":50,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":5,\"col\":6,\"id\":51,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":5,\"col\":7,\"id\":52,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5},{\"box\":5,\"col\":8,\"id\":53,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":5,\"value\":7},{\"box\":6,\"col\":0,\"id\":54,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6},{\"box\":6,\"col\":1,\"id\":55,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":6,\"value\":5},{\"box\":6,\"col\":2,\"id\":56,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6},{\"box\":7,\"col\":3,\"id\":57,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6},{\"box\":7,\"col\":4,\"id\":58,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6,\"value\":2},{\"box\":7,\"col\":5,\"id\":59,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":6,\"value\":7},{\"box\":8,\"col\":6,\"id\":60,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6,\"value\":8},{\"box\":8,\"col\":7,\"id\":61,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":6,\"value\":3},{\"box\":8,\"col\":8,\"id\":62,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":6,\"value\":6},{\"box\":6,\"col\":0,\"id\":63,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7},{\"box\":6,\"col\":1,\"id\":64,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":7,\"value\":6},{\"box\":6,\"col\":2,\"id\":65,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":7,\"value\":2},{\"box\":7,\"col\":3,\"id\":66,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7},{\"box\":7,\"col\":4,\"id\":67,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7},{\"box\":7,\"col\":5,\"id\":68,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7},{\"box\":8,\"col\":6,\"id\":69,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7,\"value\":7},{\"box\":8,\"col\":7,\"id\":70,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":7,\"value\":4},{\"box\":8,\"col\":8,\"id\":71,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":7,\"value\":1},{\"box\":6,\"col\":0,\"id\":72,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":8,\"value\":8},{\"box\":6,\"col\":1,\"id\":73,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":8,\"value\":7},{\"box\":6,\"col\":2,\"id\":74,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8},{\"box\":7,\"col\":3,\"id\":75,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8},{\"box\":7,\"col\":4,\"id\":76,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8,\"value\":4},{\"box\":7,\"col\":5,\"id\":77,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8,\"value\":6},{\"box\":8,\"col\":6,\"id\":78,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8,\"value\":5},{\"box\":8,\"col\":7,\"id\":79,\"isError\":false,\"isGiven\":true,\"notes\":[],\"row\":8,\"value\":9},{\"box\":8,\"col\":8,\"id\":80,\"isError\":false,\"isGiven\":false,\"notes\":[],\"row\":8,\"value\":2}]}"
@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val generator: SudokuGenerator,
    private val hintGenerator: HintGenerator,
    private val solver: Solver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(board = Board.createEmpty()))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    private val history = ArrayDeque<Board>()

    init {
        val args = savedStateHandle.toRoute<GameRoute>()

        if (args.createNew) {
            startNewGame(args.difficulty)
        } else {
            initializeGame()
        }
    }

    private fun initializeGame() {

        if (BuildConfig.DEBUG && DEBUG_BOARD_JSON.isNotEmpty()) {
            val debugBoard = com.google.gson.Gson().fromJson(DEBUG_BOARD_JSON, Board::class.java)

            val startingCells = debugBoard.cells.map {
                if (it.isGiven) it else it.copy(value = null, notes = emptySet())
            }
            val startingBoard = Board(startingCells)

            val solveResult = solver.solve(startingBoard)
            val solvedDebugBoard = if (solveResult is SolveResult.Success) {
                solveResult.board
            } else {
                println("[DEBUG] 🛑 EL TABLERO INYECTADO NO TIENE SOLUCIÓN VÁLIDA")
                null
            }

            _uiState.update {
                it.copy(
                    board = debugBoard,
                    solvedBoard = solvedDebugBoard,
                    isLoading = false,
                    completedNumbers = calculateCompletedNumbers(debugBoard)
                )
            }
            return
        }

        viewModelScope.launch {
            // recuperamos partida guardada (si hay)
            val savedGame = repository.getSavedGame().firstOrNull()

            if (savedGame != null) {
                _uiState.update {
                    it.copy(
                        board = savedGame.board,
                        solvedBoard = savedGame.solvedBoard,
                        elapsedTimeSeconds = savedGame.elapsedTimeSeconds,
                        difficulty = savedGame.difficulty,
                        isLoading = false,
                        completedNumbers = calculateCompletedNumbers(savedGame.board)
                    )
                }
                resumeTimer()
            } else {
                startNewGame(Difficulty.MEDIUM) // Fail-Safe si no carga la partida guardada
            }
        }
    }

    fun onCellClicked(cellId: Int) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedCellId == cellId) null else cellId

            val selectedCell =
                if (newSelection != null) currentState.board.cells.find { it.id == newSelection } else null

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
        val currentSelectedId = _uiState.value.selectedCellId ?: return
        var justWon = false

        _uiState.update { currentState ->

            val boardAfterMove = currentState.board.playMove(
                cellId = currentSelectedId,
                number = number,
                isNoteMode = currentState.isNoteMode
            )

            if (boardAfterMove == currentState.board) {
                return@update currentState
            }

            val finalBoard = if (!currentState.isNoteMode) {
                autoCleanNotes(boardAfterMove, currentSelectedId, number)
            } else {
                boardAfterMove
            }

            saveToHistory()

            justWon = finalBoard.isSolved()

            currentState.copy(
                board = finalBoard,
                isComplete = justWon,
                completedNumbers = calculateCompletedNumbers(finalBoard)
            )
        }

        if (justWon) handleVictory() else saveGame()
    }

    private fun autoCleanNotes(board: Board, cellId: Int, number: Int): Board {
        val peers = board.getPeers(cellId)

        val newCells = board.cells.map { cell ->
            if (peers.contains(cell.id) && cell.notes.contains(number)) {
                cell.copy(notes = cell.notes - number)
            } else {
                cell
            }
        }
        return Board(newCells)
    }

    private fun handleVictory() {
        pauseTimer()

        val finalTime = _uiState.value.elapsedTimeSeconds
        val difficulty = _uiState.value.difficulty

        viewModelScope.launch {
            repository.saveVictory(finalTime, difficulty)
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

            currentState.copy(
                board = newBoard,
                completedNumbers = calculateCompletedNumbers(newBoard))
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

            val solution = currentState.solvedBoard ?: return@launch

            // para no guardar tableros vacíos
            if (currentState.board.cells.isNotEmpty()) {
                repository.saveGame(
                    SavedGame(
                        board = currentState.board,
                        solvedBoard = solution,
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
            it.copy(
                board = previousBoard,
                completedNumbers = calculateCompletedNumbers(previousBoard)
            )
        }
        saveGame()
    }

    fun startNewGame(difficulty: Difficulty) {
        viewModelScope.launch(Dispatchers.Default) {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    difficulty = difficulty
                )
            }

            history.clear()

            val minLoadingTimeJob = launch {
                delay(2000)
            }

            val puzzle = generator.generate(difficulty)

            minLoadingTimeJob.join()

            _uiState.update {
                it.copy(
                    board = puzzle.board,
                    solvedBoard = puzzle.solvedBoard,
                    difficulty = puzzle.difficulty,
                    elapsedTimeSeconds = 0,
                    isComplete = false,
                    isNoteMode = false,
                    selectedCellId = null,
                    highlightedCellIds = emptySet(),
                    sameValueCellIds = emptySet(),
                    isLoading = false,
                    completedNumbers = calculateCompletedNumbers(puzzle.board)
                )
            }

            saveGame()
            resumeTimer()
        }
    }

    fun onRequestHint() {
        println("[HINT_DEBUG] --- BOTON PISTA PRESIONADO ---")
        val currentState = _uiState.value
        val solution = currentState.solvedBoard

        if (solution == null) {
            println("[HINT_DEBUG] 🛑 ERROR: solvedBoard es NULL. Abortando.")
            return
        }

        val errorCount = getMistakeCount(currentState.board, solution)
        if (errorCount > 0) {
            println("[HINT_DEBUG] 🛑 ERROR: Hay $errorCount casilleros mal puestos. Abortando.")
            _uiState.update { it.copy(showMistakeError = true, mistakeCount = errorCount) }
            return
        }

        println("[HINT_DEBUG] Validaciones pasadas. Llamando al HintGenerator...")
        viewModelScope.launch(Dispatchers.Default) {

            val hints = hintGenerator.findAllHints(currentState.board)

            _uiState.update {
                if (hints.isNotEmpty()) {
                    println("[HINT_DEBUG] UI Actualizada con ${hints.size} pistas.")
                    it.copy(
                        activeHints = hints,
                        currentHintIndex = 0,
                        showNoHintFound = false
                    )
                } else {
                    println("[HINT_DEBUG] UI Actualizada: No se encontraron pistas.")
                    it.copy(showNoHintFound = true)
                }
            }
        }
    }

    fun onNextHint() {
        _uiState.update {
            if (it.activeHints.isNotEmpty()) {
                val nextIndex = (it.currentHintIndex + 1) % it.activeHints.size
                it.copy(currentHintIndex = nextIndex)
            } else it
        }
    }

    fun onPrevHint() {
        _uiState.update {
            if (it.activeHints.isNotEmpty()) {
                val prevIndex = if (it.currentHintIndex - 1 < 0) it.activeHints.lastIndex else it.currentHintIndex - 1
                it.copy(currentHintIndex = prevIndex)
            } else it
        }
    }

    private fun getMistakeCount(current: Board, solution: Board): Int {
        return current.cells.count { cell ->
            // Cuenta si tiene valor, no es inicial y no coincide con la solución
            cell.value != null && !cell.isGiven && cell.value != solution.cells[cell.id].value
        }
    }

    fun onDismissHint() {
        _uiState.update { it.copy(activeHints = emptyList(), showNoHintFound = false) }
    }

    fun onRevealMistakes() {
        _uiState.update { state ->
            val solution = state.solvedBoard ?: return@update state

            val markedCells = state.board.cells.map { cell ->
                if (cell.value != null && !cell.isGiven && cell.value != solution.cells[cell.id].value) {
                    cell.copy(isError = true)
                } else {
                    cell
                }
            }

            state.copy(
                board = Board(markedCells),
                showMistakeError = false
            )
        }

        saveGame()
    }

    fun onDismissMistakeDialog() {
        _uiState.update { it.copy(showMistakeError = false) }
    }

    private fun calculateCompletedNumbers(board: Board): Set<Int> {
        val counts = mutableMapOf<Int, Int>()

        board.cells.forEach { cell ->
            if (cell.value != null) {
                counts[cell.value] = (counts[cell.value] ?: 0) + 1
            }
        }

        return counts.filter { it.value >= 9 }.keys
    }

    fun getDebugDump(): String {
        val currentBoard = _uiState.value.board
        val visualGrid = currentBoard.toGridString()
        val jsonString = com.google.gson.Gson().toJson(currentBoard)

        return """
        === SUDOKU DEBUG DUMP ===
        $visualGrid
        -- JSON --
        $jsonString
    """.trimIndent()
    }

    override fun onCleared() {
        super.onCleared()
        saveGame()
        timerJob?.cancel()
    }
}