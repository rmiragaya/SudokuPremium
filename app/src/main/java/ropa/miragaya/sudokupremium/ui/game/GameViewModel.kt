package ropa.miragaya.sudokupremium.ui.game

import android.app.Activity
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider
import ropa.miragaya.sudokupremium.crash.CrashReporter
import ropa.miragaya.sudokupremium.domain.generator.PuzzleGenerator
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.hints.HintProvider
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardSource
import ropa.miragaya.sudokupremium.domain.stats.UserStatsRepository
import ropa.miragaya.sudokupremium.monetization.PremiumEntitlementRepository
import ropa.miragaya.sudokupremium.monetization.PremiumPurchaseState
import ropa.miragaya.sudokupremium.monetization.RewardedHintAdManager
import ropa.miragaya.sudokupremium.monetization.RewardedHintAdResult
import ropa.miragaya.sudokupremium.settings.AppSettingsRepository
import ropa.miragaya.sudokupremium.ui.model.PremiumStatusMessage
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import ropa.miragaya.sudokupremium.util.DispatcherProvider

private const val USE_DEBUG_BOARD = false

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val userStatsRepository: UserStatsRepository,
    private val generator: PuzzleGenerator,
    private val hintProvider: HintProvider,
    private val solver: Solver,
    private val debugBoardSource: DebugBoardSource,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val remoteConfigProvider: RemoteConfigProvider,
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
    private val rewardedHintAdManager: RewardedHintAdManager,
    private val appSettingsRepository: AppSettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tag = this::class.java.simpleName

    private val _uiState = MutableStateFlow(GameUiState(board = Board.createEmpty()))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var mistakesRevealedForGame = 0

    private val history = ArrayDeque<Board>()

    init {
        val args = savedStateHandle.toGameRoute()
        observePremiumEntitlement()
        observePurchaseState()
        observeAppSettings()
        premiumEntitlementRepository.refreshPurchases()

        _uiState.update {
            it.copy(freeHintsPerGame = remoteConfigProvider.freeHintsPerGame)
        }

        if (args.createNew) {
            startNewGame(args.difficulty)
        } else {
            initializeGame()
        }
    }

    private fun initializeGame() {
        if (BuildConfig.DEBUG && USE_DEBUG_BOARD) {
            // Tiene nakedTriple y Y-Wing.
            val debugBoard = debugBoardSource.loadBoardFromJson("board1-intersection-removal-1.txt")
//            val debugBoard = debugBoardLoader.loadBoardFromJson("board2-naked-pair-1.txt")
//            val debugBoard = debugBoardLoader.loadBoardFromJson("board2-naked-pair-2.txt")
//            val debugBoard = debugBoardLoader.loadBoardFromGrid()

            if (debugBoard == null) {
                Log.e(tag, "No se pudo cargar el debug board")
                crashReporter.recordNonFatal(IllegalStateException("No se pudo cargar el debug board"))
                return
            }

            val startingCells = debugBoard.cells.map {
                if (it.isGiven) it else it.copy(value = null, notes = emptySet())
            }
            val startingBoard = Board(startingCells)

            val solveResult = solver.solve(startingBoard)
            val solvedDebugBoard = if (solveResult is SolveResult.Success) {
                solveResult.board
            } else {
                null
            }

            _uiState.update {
                it.copy(
                    board = debugBoard,
                    solvedBoard = solvedDebugBoard,
                    isLoading = false,
                    completedNumbers = calculateCompletedNumbers(debugBoard, solvedDebugBoard)
                )
            }
            updateCrashGameContext()
            return
        }

        viewModelScope.launch(dispatcherProvider.io) {
            // recuperamos partida guardada (si hay)
            val savedGame = repository.getSavedGame().firstOrNull()

            if (savedGame != null) {
                _uiState.update {
                    it.copy(
                        board = savedGame.board,
                        solvedBoard = savedGame.solvedBoard,
                        elapsedTimeSeconds = savedGame.elapsedTimeSeconds,
                        difficulty = savedGame.difficulty,
                        hintsUsed = savedGame.hintsUsed,
                        rewardedHintsAvailable = savedGame.rewardedHintsAvailable,
                        freeHintsPerGame = remoteConfigProvider.freeHintsPerGame,
                        isLoading = false,
                        completedNumbers = calculateCompletedNumbers(savedGame.board, savedGame.solvedBoard)
                    )
                }
                mistakesRevealedForGame = 0
                updateCrashGameContext()
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

            justWon = isSolvedAgainstSolution(finalBoard, currentState.solvedBoard)

            currentState.copy(
                board = finalBoard,
                isComplete = justWon,
                completedNumbers = calculateCompletedNumbers(finalBoard, currentState.solvedBoard),
                activeHints = emptyList(),
                currentHintIndex = 0
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
        updateCrashGameContext()
        crashReporter.log("Game completed: difficulty=$difficulty, elapsedSeconds=$finalTime")
        analyticsTracker.logGameCompleted(
            difficulty = difficulty,
            elapsedSeconds = finalTime,
            hintsUsed = _uiState.value.hintsUsed,
            mistakesRevealed = mistakesRevealedForGame
        )

        viewModelScope.launch(dispatcherProvider.io) {
            userStatsRepository.trackGameCompleted(
                difficulty = difficulty,
                elapsedSeconds = finalTime,
                hintsUsed = _uiState.value.hintsUsed,
                mistakesRevealed = mistakesRevealedForGame
            )
            repository.saveVictory(finalTime, difficulty)
        }
    }

    fun onDeleteInput() {
        val currentSelectedId = _uiState.value.selectedCellId ?: return

        _uiState.update { currentState ->
            val currentBoard = currentState.board
            val cell = currentBoard.cells.find { it.id == currentSelectedId } ?: return@update currentState

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
                completedNumbers = calculateCompletedNumbers(newBoard, currentState.solvedBoard),
                activeHints = emptyList(),
                currentHintIndex = 0
            )
        }

        saveGame()
    }

    fun resumeTimer() {
        if (_uiState.value.isLoading || _uiState.value.isComplete) return
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch(dispatcherProvider.main) {
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
        viewModelScope.launch(dispatcherProvider.io) {
            val currentState = _uiState.value

            if (currentState.isComplete) return@launch

            val solution = currentState.solvedBoard ?: return@launch

            // para no guardar tableros vacíos
            if (currentState.board.cells.isNotEmpty()) {
                repository.saveGame(
                    SavedGame(
                        board = currentState.board,
                        solvedBoard = solution,
                        elapsedTimeSeconds = currentState.elapsedTimeSeconds,
                        difficulty = currentState.difficulty,
                        hintsUsed = currentState.hintsUsed,
                        rewardedHintsAvailable = currentState.rewardedHintsAvailable
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
                completedNumbers = calculateCompletedNumbers(previousBoard, it.solvedBoard),
                activeHints = emptyList(),
                currentHintIndex = 0
            )
        }
        saveGame()
    }

    fun startNewGame(difficulty: Difficulty) {
        pauseTimer()
        crashReporter.log("Starting new game: difficulty=$difficulty")

        viewModelScope.launch(dispatcherProvider.default) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    difficulty = difficulty,
                    isComplete = false,
                    activeHints = emptyList(),
                    currentHintIndex = 0,
                    showNoHintFound = false,
                    showMistakeError = false,
                    showHintLimitSheet = false,
                    showPremiumSheet = false,
                    showRewardedHintError = false
                )
            }

            history.clear()
            mistakesRevealedForGame = 0

            val puzzle = generator.generate(difficulty)
            delay(remoteConfigProvider.newGameLoadingMinDurationMs)

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
                    activeHints = emptyList(),
                    currentHintIndex = 0,
                    showNoHintFound = false,
                    showMistakeError = false,
                    showHintLimitSheet = false,
                    showPremiumSheet = false,
                    showRewardedHintError = false,
                    hintsUsed = 0,
                    rewardedHintsAvailable = 0,
                    freeHintsPerGame = remoteConfigProvider.freeHintsPerGame,
                    isLoading = false,
                    completedNumbers = calculateCompletedNumbers(puzzle.board, puzzle.solvedBoard)
                )
            }

            updateCrashGameContext()
            analyticsTracker.logNewGameStarted(puzzle.difficulty)
            userStatsRepository.trackGameStarted(puzzle.difficulty)
            saveGame()
            resumeTimer()
        }
    }

    fun onRequestHint() {
        if (!canRequestHintNow()) {
            val state = _uiState.value
            analyticsTracker.logHintLimitReached(state.difficulty, state.hintsUsed)
            _uiState.update { it.copy(showHintLimitSheet = true, showRewardedHintError = false) }
            updateCrashGameContext()
            return
        }

        requestHintAfterAccessGranted()
    }

    private fun requestHintAfterAccessGranted() {
        val currentState = _uiState.value
        val solution = currentState.solvedBoard
        crashReporter.log("Hint requested: difficulty=${currentState.difficulty}")

        if (solution == null) {
            Log.d(tag, "onRequestHint: solution == null")
            crashReporter.recordNonFatal(IllegalStateException("onRequestHint called without solution"))
            return
        }

        val errorCount = getMistakeCount(currentState.board, solution)
        analyticsTracker.logHintRequested(
            difficulty = currentState.difficulty,
            elapsedSeconds = currentState.elapsedTimeSeconds,
            hasMistakes = errorCount > 0
        )

        if (errorCount > 0) {
            _uiState.update { it.copy(showMistakeError = true, mistakeCount = errorCount) }
            return
        }

        viewModelScope.launch(dispatcherProvider.default) {
            val hints = hintProvider.findAllHints(currentState.board)

            if (hints.isNotEmpty()) {
                val shouldConsumeRewardedHint = shouldConsumeRewardedHint(currentState)
                val newHintsUsed = currentState.hintsUsed + 1
                val newRewardedHintsAvailable = if (shouldConsumeRewardedHint) {
                    (currentState.rewardedHintsAvailable - 1).coerceAtLeast(0)
                } else {
                    currentState.rewardedHintsAvailable
                }
                analyticsTracker.logHintShown(
                    difficulty = currentState.difficulty,
                    strategyName = hints.first().strategyName,
                    hintCount = hints.size
                )
                _uiState.update {
                    it.copy(
                        activeHints = hints,
                        currentHintIndex = 0,
                        showNoHintFound = false,
                        showHintLimitSheet = false,
                        showRewardedHintError = false,
                        hintsUsed = newHintsUsed,
                        rewardedHintsAvailable = newRewardedHintsAvailable
                    )
                }
                updateCrashGameContext()
                saveGame()
            } else {
                _uiState.update { it.copy(showNoHintFound = true) }
            }
        }
    }

    private fun canRequestHintNow(): Boolean {
        val state = _uiState.value
        return state.isPremium ||
            state.hintsUsed < state.freeHintsPerGame ||
            state.rewardedHintsAvailable > 0
    }

    private fun shouldConsumeRewardedHint(state: GameUiState): Boolean {
        return !state.isPremium &&
            state.hintsUsed >= state.freeHintsPerGame &&
            state.rewardedHintsAvailable > 0
    }

    fun onDismissHintLimitSheet() {
        _uiState.update { it.copy(showHintLimitSheet = false, showRewardedHintError = false) }
    }

    fun onUnlockPremiumClick() {
        _uiState.update {
            it.copy(
                showHintLimitSheet = false,
                showSettingsDialog = false,
                showPremiumSheet = true
            )
        }
    }

    fun onDismissPremiumSheet() {
        _uiState.update { it.copy(showPremiumSheet = false, premiumStatusMessage = null) }
    }

    fun onWatchRewardedHintAdClick(activity: Activity?) {
        if (activity == null) {
            _uiState.update { it.copy(showRewardedHintError = true) }
            analyticsTracker.logRewardedHintAdFailed("Activity unavailable")
            return
        }

        _uiState.update { it.copy(isRewardedHintLoading = true, showRewardedHintError = false) }
        analyticsTracker.logRewardedHintAdRequested()

        rewardedHintAdManager.showRewardedHintAd(activity) { result ->
            when (result) {
                RewardedHintAdResult.Earned -> {
                    analyticsTracker.logRewardedHintAdEarned()
                    _uiState.update {
                        it.copy(
                            rewardedHintsAvailable = it.rewardedHintsAvailable + 1,
                            isRewardedHintLoading = false,
                            showHintLimitSheet = false,
                            showRewardedHintError = false
                        )
                    }
                    updateCrashGameContext()
                    requestHintAfterAccessGranted()
                }

                RewardedHintAdResult.Dismissed -> {
                    _uiState.update { it.copy(isRewardedHintLoading = false) }
                }

                is RewardedHintAdResult.Failed -> {
                    analyticsTracker.logRewardedHintAdFailed(result.reason)
                    _uiState.update {
                        it.copy(
                            isRewardedHintLoading = false,
                            showRewardedHintError = true
                        )
                    }
                }
            }
        }
    }

    fun onPurchasePremiumClick(activity: Activity?) {
        if (activity == null) {
            _uiState.update { it.copy(premiumStatusMessage = PremiumStatusMessage.PURCHASE_NOT_STARTED) }
            return
        }

        premiumEntitlementRepository.launchPremiumPurchase(activity)
    }

    fun onRestorePremiumClick() {
        premiumEntitlementRepository.refreshPurchases()
    }

    fun onSettingsClick() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun onDismissSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun onHapticsEnabledChanged(enabled: Boolean) {
        appSettingsRepository.setHapticsEnabled(enabled)
    }

    fun onNextHint() {
        _uiState.update {
            if (it.activeHints.isNotEmpty() && it.currentHintIndex < it.activeHints.lastIndex) {
                it.copy(currentHintIndex = it.currentHintIndex + 1)
            } else {
                it
            }
        }
    }

    fun onPrevHint() {
        _uiState.update {
            if (it.activeHints.isNotEmpty() && it.currentHintIndex > 0) {
                it.copy(currentHintIndex = it.currentHintIndex - 1)
            } else {
                it
            }
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
        val currentState = _uiState.value
        val solution = currentState.solvedBoard
        if (solution != null) {
            mistakesRevealedForGame += getMistakeCount(currentState.board, solution)
        }

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

        updateCrashGameContext()
        saveGame()
    }

    fun onCrashlyticsTestCrashClick() {
        if (!BuildConfig.DEBUG) return

        crashReporter.throwTestCrash()
    }

    fun onDebugResetPremiumClick() {
        if (!BuildConfig.DEBUG) return

        premiumEntitlementRepository.resetPremiumForDebug()
    }

    fun onDebugFillCandidatesClick() {
        if (!BuildConfig.DEBUG) return

        _uiState.update { state ->
            state.copy(
                board = state.board.initializeCandidates(),
                activeHints = emptyList(),
                currentHintIndex = 0
            )
        }
        saveGame()
    }

    fun onDebugPrepareVictoryClick() {
        if (!BuildConfig.DEBUG) return

        val solution = _uiState.value.solvedBoard ?: return
        val targetCellId = _uiState.value.board.cells.lastOrNull { !it.isGiven }?.id ?: return

        _uiState.update { state ->
            val cells = state.board.cells.map { cell ->
                if (cell.isGiven || cell.id == targetCellId) {
                    cell.copy(isError = false, notes = emptySet())
                } else {
                    cell.copy(
                        value = solution.cells[cell.id].value,
                        isError = false,
                        notes = emptySet()
                    )
                }
            }
            val debugBoard = Board(cells)

            state.copy(
                board = debugBoard,
                selectedCellId = targetCellId,
                highlightedCellIds = state.board.getPeers(targetCellId),
                sameValueCellIds = emptySet(),
                isNoteMode = false,
                activeHints = emptyList(),
                currentHintIndex = 0,
                showNoHintFound = false,
                showMistakeError = false,
                completedNumbers = calculateCompletedNumbers(debugBoard, solution)
            )
        }
        saveGame()
    }

    fun onDismissMistakeDialog() {
        _uiState.update { it.copy(showMistakeError = false) }
    }

    private fun isSolvedAgainstSolution(board: Board, solution: Board?): Boolean {
        if (solution == null || board.cells.any { it.value == null }) return false

        return board.cells.all { cell ->
            cell.value == solution.cells[cell.id].value
        }
    }

    private fun calculateCompletedNumbers(board: Board, solution: Board?): Set<Int> {
        val counts = mutableMapOf<Int, Int>()

        board.cells.forEach { cell ->
            if (cell.value != null && (solution == null || cell.value == solution.cells[cell.id].value)) {
                counts[cell.value] = (counts[cell.value] ?: 0) + 1
            }
        }

        return counts.filter { it.value >= 9 }.keys
    }

    private fun updateCrashGameContext() {
        val state = _uiState.value
        crashReporter.setGameContext(
            difficulty = state.difficulty,
            elapsedSeconds = state.elapsedTimeSeconds,
            hintsUsed = state.hintsUsed,
            rewardedHintsAvailable = state.rewardedHintsAvailable,
            isPremium = state.isPremium,
            mistakesRevealed = mistakesRevealedForGame,
            isComplete = state.isComplete
        )
    }

    private fun observePremiumEntitlement() {
        viewModelScope.launch(dispatcherProvider.main) {
            premiumEntitlementRepository.isPremium.collect { isPremium ->
                _uiState.update {
                    it.copy(
                        isPremium = isPremium,
                        showHintLimitSheet = if (isPremium) false else it.showHintLimitSheet
                    )
                }
                updateCrashGameContext()
            }
        }
    }

    private fun observePurchaseState() {
        viewModelScope.launch(dispatcherProvider.main) {
            premiumEntitlementRepository.purchaseState.collect { purchaseState ->
                val message = when (purchaseState) {
                    PremiumPurchaseState.Purchased -> PremiumStatusMessage.PREMIUM_ACTIVATED
                    PremiumPurchaseState.Restored -> null
                    PremiumPurchaseState.Pending -> PremiumStatusMessage.PURCHASE_PENDING
                    PremiumPurchaseState.Canceled -> PremiumStatusMessage.PURCHASE_CANCELED
                    is PremiumPurchaseState.Failed -> PremiumStatusMessage.PREMIUM_ACTIVATION_FAILED
                    PremiumPurchaseState.Idle,
                    PremiumPurchaseState.Loading -> null
                }

                _uiState.update {
                    it.copy(
                        showPremiumSheet = if (purchaseState == PremiumPurchaseState.Purchased ||
                            purchaseState == PremiumPurchaseState.Restored
                        ) {
                            false
                        } else {
                            it.showPremiumSheet
                        },
                        premiumStatusMessage = message
                    )
                }
            }
        }
    }

    private fun observeAppSettings() {
        viewModelScope.launch(dispatcherProvider.main) {
            appSettingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(hapticsEnabled = settings.hapticsEnabled) }
            }
        }
    }

    fun getDebugDump(): String {
        if (!BuildConfig.DEBUG) return ""

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

private fun SavedStateHandle.toGameRoute(): GameRoute {
    return runCatching {
        toRoute<GameRoute>()
    }.getOrElse { cause ->
        val hasTestRouteArgs = keys().contains("createNew") || keys().contains("difficulty")
        if (!hasTestRouteArgs) throw cause

        val createNew = get<Boolean>("createNew")
            ?: get<String>("createNew")?.toBooleanStrictOrNull()
            ?: false

        val difficulty = when (val rawDifficulty = get<Any>("difficulty")) {
            is Difficulty -> rawDifficulty
            is String -> runCatching { Difficulty.valueOf(rawDifficulty) }.getOrDefault(Difficulty.EASY)
            else -> Difficulty.EASY
        }

        GameRoute(createNew = createNew, difficulty = difficulty)
    }
}
