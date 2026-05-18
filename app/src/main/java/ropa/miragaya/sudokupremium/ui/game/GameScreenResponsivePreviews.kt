package ropa.miragaya.sudokupremium.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuHint

private const val PREVIEW_BACKGROUND = 0xFF010413

@Preview(
    name = "Game - Samsung S23",
    widthDp = 360,
    heightDp = 780,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Game - Pixel 7 Pro",
    widthDp = 412,
    heightDp = 915,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Game - Compact height",
    widthDp = 360,
    heightDp = 720,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Composable
private fun GameScreenResponsivePreview() {
    GameScreenPreviewContent(uiState = gamePreviewState())
}

@Preview(
    name = "Tutorial beginner - Samsung S23",
    widthDp = 360,
    heightDp = 780,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Tutorial beginner - Pixel 7 Pro",
    widthDp = 412,
    heightDp = 915,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Tutorial beginner - Compact height",
    widthDp = 360,
    heightDp = 720,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Composable
private fun BeginnerTutorialResponsivePreview() {
    GameScreenPreviewContent(uiState = beginnerTutorialPreviewState())
}

@Preview(
    name = "Hint long text - Samsung S23",
    widthDp = 360,
    heightDp = 780,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Hint long text - Pixel 7 Pro",
    widthDp = 412,
    heightDp = 915,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Preview(
    name = "Hint long text - Compact height",
    widthDp = 360,
    heightDp = 720,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND
)
@Composable
private fun LongHintResponsivePreview() {
    GameScreenPreviewContent(uiState = longHintPreviewState())
}

@Composable
private fun GameScreenPreviewContent(uiState: GameUiState) {
    GameContent(
        uiState = uiState,
        onCellClick = {},
        onNumberInput = {},
        onDeleteInput = {},
        onBackClick = {},
        onToggleNoteMode = {},
        onHintClick = {},
        onGetDebugDumpClick = {},
        onCrashlyticsTestCrashClick = {},
        onDebugFillCandidatesClick = {},
        onDebugPrepareVictoryClick = {},
        onDebugResetPremiumClick = {},
        onSettingsClick = {},
        onOpenTechniquesClick = {},
        onHowToPlayClick = {},
        onOpenTechniqueClick = {},
        currentHintIndex = uiState.currentHintIndex,
        totalHints = uiState.activeHints.size,
        onDismissHint = {},
        onNextHint = {},
        onPrevHint = {},
        onSkipGuidedTutorial = {},
        onNextGuidedTutorialStep = {},
        onVictoryNewGameClick = {},
        onUndo = {}
    )
}

private fun gamePreviewState(): GameUiState {
    return GameUiState(
        board = previewBoard(),
        elapsedTimeSeconds = 60,
        difficulty = Difficulty.EASY,
        selectedCellId = 23,
        highlightedCellIds = setOf(23),
        completedNumbers = emptySet(),
        isLoading = false
    )
}

private fun beginnerTutorialPreviewState(): GameUiState {
    val board = previewBoard()
    val targetCell = 23
    val hint = SudokuHint(
        strategyName = "Naked Single",
        description = "La casilla resaltada en amarillo solo tiene un candidato posible: 5. " +
            "Como todos los demás números ya quedan descartados por su fila, columna o caja, esa casilla debe ser 5.",
        targetCellIndex = targetCell,
        valueToSet = 5,
        highlightCells = listOf(targetCell),
        stepBoard = board
    )

    return GameUiState(
        board = board,
        elapsedTimeSeconds = 24,
        difficulty = Difficulty.EASY,
        selectedCellId = targetCell,
        highlightedCellIds = setOf(targetCell),
        completedNumbers = emptySet(),
        isLoading = false,
        guidedTutorial = GuidedTutorialUiState(
            phase = GuidedTutorialPhase.MOVE,
            currentStep = 1,
            totalSteps = 4,
            currentHint = hint
        ),
        tutorialInputMessage = "Tocá la casilla resaltada y después el 5 resaltado."
    )
}

private fun longHintPreviewState(): GameUiState {
    val board = previewBoardWithCandidates()
    val hint = SudokuHint(
        strategyName = "Naked Single",
        description = "La casilla resaltada en amarillo solo tiene un candidato posible: 2. " +
            "Como todos los demás números ya quedan descartados por su fila, columna o caja, esa casilla debe ser 2. " +
            "Este texto largo sirve para verificar que la pista no empuje el tablero fuera de una pantalla real.",
        targetCellIndex = 22,
        valueToSet = 2,
        highlightCells = listOf(22),
        stepBoard = board
    )

    return GameUiState(
        board = board,
        elapsedTimeSeconds = 50,
        difficulty = Difficulty.EASY,
        selectedCellId = 22,
        highlightedCellIds = setOf(22),
        completedNumbers = setOf(5),
        activeHints = listOf(hint),
        currentHintIndex = 0,
        isLoading = false
    )
}

private fun previewBoard(): Board {
    return Board.fromGridString(
        """
        2....1...
        183..9..7
        .5..7...2
        9...8.2..
        .61...8..
        8......69
        ...76.4..
        ...1.26.5
        .....4...
        """.trimIndent()
    )
}

private fun previewBoardWithCandidates(): Board {
    val board = previewBoard()

    return board.copy(
        cells = board.cells.map { cell ->
            if (cell.value == null) {
                val notes = when (cell.id) {
                    22 -> setOf(2)
                    1, 2, 9, 18 -> setOf(4, 7, 9)
                    else -> setOf(1, 2, 3, 5, 7, 8, 9).filter { it != ((cell.id % 9) + 1) }.take(4).toSet()
                }
                cell.copy(notes = notes)
            } else {
                cell
            }
        }
    )
}
