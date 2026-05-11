package ropa.miragaya.sudokupremium

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.analytics.TechniqueOpenSource
import ropa.miragaya.sudokupremium.config.RemoteConfigDefaults
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider
import ropa.miragaya.sudokupremium.crash.CrashReporter
import ropa.miragaya.sudokupremium.domain.generator.PuzzleGenerator
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.hints.HintProvider
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardSource
import ropa.miragaya.sudokupremium.domain.stats.UserStatsRepository
import ropa.miragaya.sudokupremium.monetization.PremiumEntitlementRepository
import ropa.miragaya.sudokupremium.monetization.PremiumPurchaseState
import ropa.miragaya.sudokupremium.monetization.RewardedHintAdManager
import ropa.miragaya.sudokupremium.monetization.RewardedHintAdResult
import ropa.miragaya.sudokupremium.settings.AppSettings
import ropa.miragaya.sudokupremium.settings.AppSettingsRepository
import ropa.miragaya.sudokupremium.ui.game.GameViewModel
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import ropa.miragaya.sudokupremium.util.DispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)
    private val solvedBoard = Board.fromGridString(SOLVED_GRID)

    @Test
    fun `loads saved game`() = runTestWithDispatcher {
        val savedBoard = boardWithEmptyCells(80)
        val savedGame = SavedGame(
            board = savedBoard,
            solvedBoard = solvedBoard,
            elapsedTimeSeconds = 42,
            difficulty = Difficulty.HARD,
            hintsUsed = 2,
            rewardedHintsAvailable = 1
        )
        val repository = FakeGameRepository(savedGame)

        val viewModel = createViewModel(
            repository = repository,
            route = GameRoute(createNew = false)
        )
        runCurrent()

        val state = viewModel.uiState.value
        assertSame(savedBoard, state.board)
        assertSame(solvedBoard, state.solvedBoard)
        assertEquals(Difficulty.HARD, state.difficulty)
        assertEquals(42, state.elapsedTimeSeconds)
        assertEquals(2, state.hintsUsed)
        assertEquals(1, state.rewardedHintsAvailable)
        assertFalse(state.isLoading)

        viewModel.pauseTimer()
    }

    @Test
    fun `new game uses generator saves game and starts timer`() = runTestWithDispatcher {
        val puzzleBoard = boardWithEmptyCells(79, 80)
        val puzzle = SudokuPuzzle(puzzleBoard, solvedBoard, Difficulty.EXPERT)
        val generator = FakePuzzleGenerator(puzzle)
        val repository = FakeGameRepository()

        val viewModel = createViewModel(
            repository = repository,
            generator = generator,
            route = GameRoute(createNew = true, difficulty = Difficulty.EXPERT)
        )
        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(repository.savedGames.isEmpty())

        advanceTimeBy(RemoteConfigDefaults.NEW_GAME_LOADING_MIN_DURATION_MS)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(listOf(Difficulty.EXPERT), generator.requestedDifficulties)
        assertSame(puzzleBoard, state.board)
        assertSame(solvedBoard, state.solvedBoard)
        assertEquals(Difficulty.EXPERT, state.difficulty)
        assertEquals(0, state.elapsedTimeSeconds)
        assertFalse(state.isLoading)
        assertFalse(state.isComplete)
        assertNull(state.selectedCellId)
        assertTrue(state.activeHints.isEmpty())
        assertEquals(puzzleBoard, repository.savedGames.last().board)

        advanceTimeBy(1_000)
        runCurrent()

        assertEquals(1, viewModel.uiState.value.elapsedTimeSeconds)
        viewModel.pauseTimer()
    }

    @Test
    fun `timer advances and stops after victory`() = runTestWithDispatcher {
        val puzzleBoard = boardWithEmptyCells(80)
        val savedGame = SavedGame(puzzleBoard, solvedBoard, 0, Difficulty.EASY)
        val repository = FakeGameRepository(savedGame)

        val viewModel = createViewModel(repository = repository)
        runCurrent()
        advanceTimeBy(1_000)
        runCurrent()

        assertEquals(1, viewModel.uiState.value.elapsedTimeSeconds)

        viewModel.onCellClicked(80)
        viewModel.onNumberInput(solutionValue(80))
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        assertTrue(viewModel.uiState.value.isComplete)
        assertEquals(1, viewModel.uiState.value.elapsedTimeSeconds)

        viewModel.pauseTimer()
    }

    @Test
    fun `victory saves victory and does not save active game again`() = runTestWithDispatcher {
        val puzzleBoard = boardWithEmptyCells(80)
        val savedGame = SavedGame(puzzleBoard, solvedBoard, 12, Difficulty.EASY)
        val repository = FakeGameRepository(savedGame)
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onCellClicked(80)
        viewModel.onNumberInput(solutionValue(80))
        runCurrent()

        assertTrue(viewModel.uiState.value.isComplete)
        assertEquals(listOf(FakeVictory(time = 12, difficulty = Difficulty.EASY)), repository.victories)
        assertTrue(repository.savedGames.isEmpty())
        assertNull(repository.savedGameFlow.value)
    }

    @Test
    fun `undo restores previous board and saves it`() = runTestWithDispatcher {
        val puzzleBoard = boardWithEmptyCells(79, 80)
        val savedGame = SavedGame(puzzleBoard, solvedBoard, 0, Difficulty.MEDIUM)
        val repository = FakeGameRepository(savedGame)
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onCellClicked(79)
        viewModel.onNumberInput(solutionValue(79))
        runCurrent()

        assertEquals(solutionValue(79), viewModel.uiState.value.board.cells[79].value)

        viewModel.onUndo()
        runCurrent()

        assertNull(viewModel.uiState.value.board.cells[79].value)
        assertEquals(puzzleBoard, viewModel.uiState.value.board)
        assertEquals(puzzleBoard, repository.savedGames.last().board)

        viewModel.pauseTimer()
    }

    @Test
    fun `valid hints are shown and navigation is not circular`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(79, 80)
        val hints = listOf(
            SudokuHint(
                strategyName = "Naked Single",
                description = "Primera pista",
                targetCellIndex = 79,
                valueToSet = solutionValue(79),
                stepBoard = board
            ),
            SudokuHint(
                strategyName = "Hidden Single",
                description = "Segunda pista",
                targetCellIndex = 80,
                valueToSet = solutionValue(80),
                stepBoard = board
            )
        )
        val hintProvider = FakeHintProvider(hints)
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY)),
            hintProvider = hintProvider
        )
        runCurrent()

        viewModel.onRequestHint()
        runCurrent()

        assertEquals(1, hintProvider.requestCount)
        assertEquals(hints, viewModel.uiState.value.activeHints)
        assertEquals(0, viewModel.uiState.value.currentHintIndex)

        viewModel.onPrevHint()
        assertEquals(0, viewModel.uiState.value.currentHintIndex)

        viewModel.onNextHint()
        assertEquals(1, viewModel.uiState.value.currentHintIndex)

        viewModel.onNextHint()
        assertEquals(1, viewModel.uiState.value.currentHintIndex)

        viewModel.pauseTimer()
    }

    @Test
    fun `free user can request hints before limit`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val hint = SudokuHint(
            strategyName = "Naked Single",
            description = "Pista",
            targetCellIndex = 80,
            valueToSet = solutionValue(80),
            stepBoard = board
        )
        val hintProvider = FakeHintProvider(listOf(hint))
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY, hintsUsed = 2)),
            hintProvider = hintProvider
        )
        runCurrent()

        viewModel.onRequestHint()
        runCurrent()

        assertEquals(1, hintProvider.requestCount)
        assertEquals(3, viewModel.uiState.value.hintsUsed)
        assertFalse(viewModel.uiState.value.showHintLimitSheet)

        viewModel.pauseTimer()
    }

    @Test
    fun `free user at hint limit sees paywall`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val hintProvider = FakeHintProvider()
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY, hintsUsed = 3)),
            hintProvider = hintProvider
        )
        runCurrent()

        viewModel.onRequestHint()
        runCurrent()

        assertEquals(0, hintProvider.requestCount)
        assertTrue(viewModel.uiState.value.showHintLimitSheet)

        viewModel.pauseTimer()
    }

    @Test
    fun `rewarded hint earned grants and consumes one hint`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val hint = SudokuHint(
            strategyName = "Naked Single",
            description = "Pista",
            targetCellIndex = 80,
            valueToSet = solutionValue(80),
            stepBoard = board
        )
        val hintProvider = FakeHintProvider(listOf(hint))
        val adManager = FakeRewardedHintAdManager(RewardedHintAdResult.Earned)
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY, hintsUsed = 3)),
            hintProvider = hintProvider,
            rewardedHintAdManager = adManager
        )
        runCurrent()

        viewModel.onWatchRewardedHintAdClick(FakeActivity())
        runCurrent()

        assertEquals(1, hintProvider.requestCount)
        assertEquals(4, viewModel.uiState.value.hintsUsed)
        assertEquals(0, viewModel.uiState.value.rewardedHintsAvailable)
        assertTrue(viewModel.uiState.value.activeHints.isNotEmpty())

        viewModel.pauseTimer()
    }

    @Test
    fun `premium user can request hints after free limit`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val hint = SudokuHint(
            strategyName = "Naked Single",
            description = "Pista",
            targetCellIndex = 80,
            valueToSet = solutionValue(80),
            stepBoard = board
        )
        val premiumRepository = FakePremiumEntitlementRepository(isPremium = true)
        val hintProvider = FakeHintProvider(listOf(hint))
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY, hintsUsed = 3)),
            hintProvider = hintProvider,
            premiumEntitlementRepository = premiumRepository
        )
        runCurrent()

        viewModel.onRequestHint()
        runCurrent()

        assertEquals(1, hintProvider.requestCount)
        assertEquals(4, viewModel.uiState.value.hintsUsed)
        assertFalse(viewModel.uiState.value.showHintLimitSheet)

        viewModel.pauseTimer()
    }

    @Test
    fun `hints with mistakes show error and do not request hints`() = runTestWithDispatcher {
        val board = boardWithWrongEditableValue()
        val hintProvider = FakeHintProvider()
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY)),
            hintProvider = hintProvider
        )
        runCurrent()

        viewModel.onRequestHint()
        runCurrent()

        assertTrue(viewModel.uiState.value.showMistakeError)
        assertEquals(1, viewModel.uiState.value.mistakeCount)
        assertEquals(0, hintProvider.requestCount)

        viewModel.pauseTimer()
    }

    @Test
    fun `reveal mistakes marks wrong cells and saves state`() = runTestWithDispatcher {
        val board = boardWithWrongEditableValue()
        val repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY))
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onRequestHint()
        viewModel.onRevealMistakes()
        runCurrent()

        assertFalse(viewModel.uiState.value.showMistakeError)
        assertTrue(viewModel.uiState.value.board.cells[80].isError)
        assertTrue(repository.savedGames.last().board.cells[80].isError)

        viewModel.pauseTimer()
    }

    @Test
    fun `delete clears value and notes and can be undone`() = runTestWithDispatcher {
        val board = boardWithEditableCellValueAndNotes()
        val repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY))
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onCellClicked(80)
        viewModel.onDeleteInput()
        runCurrent()

        assertNull(viewModel.uiState.value.board.cells[80].value)
        assertTrue(viewModel.uiState.value.board.cells[80].notes.isEmpty())

        viewModel.onUndo()
        runCurrent()

        assertEquals(solutionValue(80), viewModel.uiState.value.board.cells[80].value)
        assertEquals(setOf(1, 2), viewModel.uiState.value.board.cells[80].notes)

        viewModel.pauseTimer()
    }

    @Test
    fun `debug fill candidates adds legal notes to empty cells`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY))
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onDebugFillCandidatesClick()
        runCurrent()

        assertEquals(setOf(solutionValue(80)), viewModel.uiState.value.board.cells[80].notes)

        viewModel.pauseTimer()
    }

    @Test
    fun `debug prepare victory leaves one editable cell empty`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(79, 80)
        val repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY))
        val viewModel = createViewModel(repository = repository)
        runCurrent()

        viewModel.onDebugPrepareVictoryClick()
        runCurrent()

        val state = viewModel.uiState.value
        val editableEmptyCells = state.board.cells.filter { !it.isGiven && it.value == null }
        assertEquals(1, editableEmptyCells.size)
        assertEquals(editableEmptyCells.single().id, state.selectedCellId)
        assertFalse(state.isComplete)

        viewModel.pauseTimer()
    }

    @Test
    fun `settings dialog toggles persisted haptics preference`() = runTestWithDispatcher {
        val board = boardWithEmptyCells(80)
        val settingsRepository = FakeAppSettingsRepository()
        val viewModel = createViewModel(
            repository = FakeGameRepository(SavedGame(board, solvedBoard, 0, Difficulty.EASY)),
            appSettingsRepository = settingsRepository
        )
        runCurrent()

        viewModel.onSettingsClick()
        assertTrue(viewModel.uiState.value.showSettingsDialog)
        assertTrue(viewModel.uiState.value.hapticsEnabled)

        viewModel.onHapticsEnabledChanged(false)
        runCurrent()

        assertFalse(settingsRepository.settings.value.hapticsEnabled)
        assertFalse(viewModel.uiState.value.hapticsEnabled)

        viewModel.onDismissSettingsDialog()
        assertFalse(viewModel.uiState.value.showSettingsDialog)

        viewModel.pauseTimer()
    }

    private fun runTestWithDispatcher(testBody: suspend TestScope.() -> Unit) {
        runTest(mainDispatcherRule.testDispatcher, testBody = testBody)
    }

    private fun createViewModel(
        repository: FakeGameRepository = FakeGameRepository(),
        generator: FakePuzzleGenerator = FakePuzzleGenerator(
            SudokuPuzzle(boardWithEmptyCells(80), solvedBoard, Difficulty.MEDIUM)
        ),
        hintProvider: FakeHintProvider = FakeHintProvider(),
        premiumEntitlementRepository: FakePremiumEntitlementRepository = FakePremiumEntitlementRepository(),
        rewardedHintAdManager: FakeRewardedHintAdManager = FakeRewardedHintAdManager(),
        appSettingsRepository: FakeAppSettingsRepository = FakeAppSettingsRepository(),
        route: GameRoute = GameRoute(createNew = false)
    ): GameViewModel {
        return GameViewModel(
            repository = repository,
            userStatsRepository = FakeUserStatsRepository(),
            generator = generator,
            hintProvider = hintProvider,
            solver = Solver(),
            debugBoardSource = FakeDebugBoardSource(),
            dispatcherProvider = dispatcherProvider,
            analyticsTracker = FakeAnalyticsTracker(),
            crashReporter = FakeCrashReporter(),
            remoteConfigProvider = FakeRemoteConfigProvider(),
            premiumEntitlementRepository = premiumEntitlementRepository,
            rewardedHintAdManager = rewardedHintAdManager,
            appSettingsRepository = appSettingsRepository,
            savedStateHandle = savedStateHandleFor(route)
        )
    }

    private fun savedStateHandleFor(route: GameRoute): SavedStateHandle {
        return SavedStateHandle(
            mapOf(
                "createNew" to route.createNew,
                "difficulty" to route.difficulty.name
            )
        )
    }

    private fun boardWithEmptyCells(vararg cellIds: Int): Board {
        val emptyCellIds = cellIds.toSet()
        val grid = SOLVED_GRID.mapIndexed { index, char ->
            if (index in emptyCellIds) '0' else char
        }.joinToString("")
        return Board.fromGridString(grid)
    }

    private fun boardWithWrongEditableValue(): Board {
        return boardWithEmptyCells(80).withCellValue(80, 1)
    }

    private fun boardWithEditableCellValueAndNotes(): Board {
        val board = boardWithEmptyCells(80).withCellValue(80, solutionValue(80))
        val cells = board.cells.map { cell ->
            if (cell.id == 80) {
                cell.copy(notes = setOf(1, 2))
            } else {
                cell
            }
        }
        return Board(cells)
    }

    private fun solutionValue(cellId: Int): Int {
        return checkNotNull(solvedBoard.cells[cellId].value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val testDispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class TestDispatcherProvider(private val dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val main: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    override fun setUserId(userId: String) = Unit

    override fun logScreenViewed(screenName: String) = Unit

    override fun logDifficultySelected(difficulty: Difficulty) = Unit

    override fun logContinueGameSelected() = Unit

    override fun logNewGameStarted(difficulty: Difficulty) = Unit

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun logGameCompleted(difficulty: Difficulty, elapsed: Long, hints: Int, mistakes: Int) = Unit

    override fun logHintRequested(difficulty: Difficulty, elapsedSeconds: Long, hasMistakes: Boolean) = Unit

    override fun logHintShown(difficulty: Difficulty, strategyName: String, hintCount: Int) = Unit

    override fun logHintLimitReached(difficulty: Difficulty, hintsUsed: Int) = Unit

    override fun logRewardedHintAdRequested() = Unit

    override fun logRewardedHintAdEarned() = Unit

    override fun logRewardedHintAdFailed(reason: String?) = Unit

    override fun logPremiumPurchaseStarted() = Unit

    override fun logPremiumPurchaseCompleted() = Unit

    override fun logPremiumPurchaseRestored() = Unit

    override fun logTechniqueOpened(techniqueId: String, source: TechniqueOpenSource) = Unit
}

private class FakeCrashReporter : CrashReporter {
    override fun setUserId(userId: String) = Unit

    override fun log(message: String) = Unit

    override fun recordNonFatal(throwable: Throwable) = Unit

    override fun setGameContext(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        rewardedHintsAvailable: Int,
        isPremium: Boolean,
        mistakesRevealed: Int,
        isComplete: Boolean
    ) = Unit

    override fun clearGameContext() = Unit

    override fun throwTestCrash() = Unit
}

private class FakeRemoteConfigProvider : RemoteConfigProvider {
    override val newGameLoadingMinDurationMs: Long = RemoteConfigDefaults.NEW_GAME_LOADING_MIN_DURATION_MS
    override val adsEnabled: Boolean = RemoteConfigDefaults.ADS_ENABLED
    override val techniquesEnabled: Boolean = RemoteConfigDefaults.TECHNIQUES_ENABLED
    override val freeHintsPerGame: Int = RemoteConfigDefaults.FREE_HINTS_PER_GAME
    override val rewardedHintsEnabled: Boolean = RemoteConfigDefaults.REWARDED_HINTS_ENABLED
    override val premiumEnabled: Boolean = RemoteConfigDefaults.PREMIUM_ENABLED
    override val rewardedHintAdUnitId: String = RemoteConfigDefaults.REWARDED_HINT_AD_UNIT_ID

    override fun initialize() = Unit

    override fun fetchAndActivate() = Unit
}

private class FakePremiumEntitlementRepository(isPremium: Boolean = false) : PremiumEntitlementRepository {
    private val premiumFlow = MutableStateFlow(isPremium)
    override val isPremium = premiumFlow

    private val purchaseStateFlow = MutableStateFlow<PremiumPurchaseState>(PremiumPurchaseState.Idle)
    override val purchaseState = purchaseStateFlow

    override fun refreshPurchases() = Unit

    override fun launchPremiumPurchase(activity: Activity) {
        premiumFlow.value = true
        purchaseStateFlow.value = PremiumPurchaseState.Purchased
    }

    override fun resetPremiumForDebug() {
        premiumFlow.value = false
        purchaseStateFlow.value = PremiumPurchaseState.Idle
    }
}

private class FakeRewardedHintAdManager(
    private val result: RewardedHintAdResult = RewardedHintAdResult.Failed("not configured")
) : RewardedHintAdManager {
    var requestCount = 0
        private set

    override fun showRewardedHintAd(activity: Activity, onResult: (RewardedHintAdResult) -> Unit) {
        requestCount++
        onResult(result)
    }
}

private class FakeAppSettingsRepository : AppSettingsRepository {
    private val settingsFlow = MutableStateFlow(AppSettings())
    override val settings = settingsFlow

    override fun setHapticsEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(hapticsEnabled = enabled)
    }
}

private class FakeActivity : Activity()

private class FakeGameRepository(initialSavedGame: SavedGame? = null) : GameRepository {
    val savedGameFlow = MutableStateFlow(initialSavedGame)
    val savedGames = mutableListOf<SavedGame>()
    val victories = mutableListOf<FakeVictory>()

    override fun getSavedGame(): Flow<SavedGame?> = savedGameFlow

    override suspend fun saveGame(game: SavedGame) {
        savedGames.add(game)
        savedGameFlow.value = game
    }

    override suspend fun saveVictory(time: Long, difficulty: Difficulty) {
        victories.add(FakeVictory(time = time, difficulty = difficulty))
        savedGameFlow.value = null
    }
}

private class FakeUserStatsRepository : UserStatsRepository {
    override fun trackGameStarted(difficulty: Difficulty) = Unit

    override fun trackGameCompleted(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        hintsUsed: Int,
        mistakesRevealed: Int
    ) = Unit
}

private data class FakeVictory(val time: Long, val difficulty: Difficulty)

private class FakePuzzleGenerator(private val puzzle: SudokuPuzzle) : PuzzleGenerator {
    val requestedDifficulties = mutableListOf<Difficulty>()

    override fun generate(targetDifficulty: Difficulty): SudokuPuzzle {
        requestedDifficulties.add(targetDifficulty)
        return puzzle
    }
}

private class FakeHintProvider(private val hints: List<SudokuHint> = emptyList()) : HintProvider {
    var requestCount = 0
        private set

    override fun findAllHints(board: Board): List<SudokuHint> {
        requestCount++
        return hints
    }
}

private class FakeDebugBoardSource : DebugBoardSource {
    override fun loadBoardFromJson(fileName: String): Board? = null

    override fun loadBoardFromGrid(): Board? = null
}

private const val SOLVED_GRID =
    "123456789" +
        "456789123" +
        "789123456" +
        "234567891" +
        "567891234" +
        "891234567" +
        "345678912" +
        "678912345" +
        "912345678"
