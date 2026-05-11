package ropa.miragaya.sudokupremium.ui.game

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.ui.game.component.GameWonDialog
import ropa.miragaya.sudokupremium.ui.game.component.HintOverlayCard
import ropa.miragaya.sudokupremium.ui.game.component.MistakeDialog
import ropa.miragaya.sudokupremium.ui.game.component.SudokuDecodingBoard
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    onOpenTechniqueClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    val lifecycleOwner = LocalLifecycleOwner.current
    val onGetDebugDumpClick = if (BuildConfig.DEBUG) {
        getDebugDump(viewModel, context)
    } else {
        {}
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.resumeTimer()
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseTimer()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (uiState.isComplete) {
        GameWonDialog(
            elapsedTimeSeconds = uiState.elapsedTimeSeconds,
            onStartNewGame = { viewModel.startNewGame(uiState.difficulty) }
        )
    }

    if (uiState.showMistakeError) {
        MistakeDialog(
            mistakeCount = uiState.mistakeCount,
            onDismiss = viewModel::onDismissMistakeDialog,
            onConfirm = viewModel::onRevealMistakes
        )
    }

    if (uiState.showHintLimitSheet) {
        HintLimitDialog(
            freeHintsPerGame = uiState.freeHintsPerGame,
            isLoading = uiState.isRewardedHintLoading,
            showError = uiState.showRewardedHintError,
            onWatchAd = { viewModel.onWatchRewardedHintAdClick(activity) },
            onUnlockPremium = viewModel::onUnlockPremiumClick,
            onDismiss = viewModel::onDismissHintLimitSheet
        )
    }

    if (uiState.showPremiumSheet) {
        PremiumDialog(
            statusMessage = uiState.premiumStatusMessage,
            onPurchase = { viewModel.onPurchasePremiumClick(activity) },
            onRestore = viewModel::onRestorePremiumClick,
            onDismiss = viewModel::onDismissPremiumSheet
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameContent(
            uiState = uiState,
            onCellClick = viewModel::onCellClicked,
            onNumberInput = viewModel::onNumberInput,
            onDeleteInput = viewModel::onDeleteInput,
            onToggleNoteMode = viewModel::toggleNoteMode,
            onUndo = viewModel::onUndo,
            onBackClick = onBackClick,
            onHintClick = viewModel::onRequestHint,
            onGetDebugDumpClick = onGetDebugDumpClick,
            onCrashlyticsTestCrashClick = viewModel::onCrashlyticsTestCrashClick,
            onDebugFillCandidatesClick = viewModel::onDebugFillCandidatesClick,
            onDebugPrepareVictoryClick = viewModel::onDebugPrepareVictoryClick,
            onOpenTechniquesClick = onOpenTechniquesClick,
            onOpenTechniqueClick = onOpenTechniqueClick,
            currentHintIndex = uiState.currentHintIndex,
            totalHints = uiState.activeHints.size,
            onDismissHint = viewModel::onDismissHint,
            onNextHint = viewModel::onNextHint,
            onPrevHint = viewModel::onPrevHint,
            modifier = modifier
        )
    }
}

@Composable
private fun getDebugDump(viewModel: GameViewModel, context: Context): () -> Unit = {
    val dumpString = viewModel.getDebugDump()

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("SudokuDump", dumpString)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, "¡JSON copiado!", Toast.LENGTH_SHORT).show()
}

@Composable
private fun HintLimitDialog(
    freeHintsPerGame: Int,
    isLoading: Boolean,
    showError: Boolean,
    onWatchAd: () -> Unit,
    onUnlockPremium: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SudokuPalette.HomePanel,
        title = {
            Text(
                text = "Pistas agotadas",
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Usaste tus $freeHintsPerGame pistas gratis de esta partida.",
                    color = SudokuPalette.TextSecondary
                )
                if (showError) {
                    Text(
                        text = "No se pudo cargar el anuncio. Proba de nuevo en un momento.",
                        color = SudokuPalette.TextError
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onWatchAd,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudokuPalette.TextAccent,
                    contentColor = SudokuPalette.ScreenBackground
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = SudokuPalette.ScreenBackground
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text("Ver anuncio para 1 pista")
            }
        },
        dismissButton = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onUnlockPremium) {
                    Text("Desbloquear Premium", color = SudokuPalette.TextAccent)
                }
                TextButton(onClick = onDismiss) {
                    Text("Ahora no", color = SudokuPalette.TextSecondary)
                }
            }
        }
    )
}

@Composable
private fun PremiumDialog(
    statusMessage: String?,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SudokuPalette.HomePanel,
        title = {
            Text(
                text = "Sudoku Premium",
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Hints ilimitadas", color = SudokuPalette.TextPrimary)
                Text("Sin anuncios para pedir pistas", color = SudokuPalette.TextPrimary)
                Text("Apoyás el desarrollo de Sudoku Premium", color = SudokuPalette.TextPrimary)
                statusMessage?.let {
                    Text(text = it, color = SudokuPalette.TextAccent)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPurchase,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudokuPalette.TextAccent,
                    contentColor = SudokuPalette.ScreenBackground
                )
            ) {
                Text("Desbloquear Premium")
            }
        },
        dismissButton = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onRestore) {
                    Text("Restaurar compra", color = SudokuPalette.TextAccent)
                }
                TextButton(onClick = onDismiss) {
                    Text("Ahora no", color = SudokuPalette.TextSecondary)
                }
            }
        }
    )
}

@Composable
fun GameContent(
    uiState: GameUiState,
    onCellClick: (Int) -> Unit,
    onNumberInput: (Int) -> Unit,
    onDeleteInput: () -> Unit,
    onToggleNoteMode: () -> Unit,
    onUndo: () -> Unit,
    onBackClick: () -> Unit,
    onHintClick: () -> Unit,
    onGetDebugDumpClick: () -> Unit,
    onCrashlyticsTestCrashClick: () -> Unit,
    onDebugFillCandidatesClick: () -> Unit,
    onDebugPrepareVictoryClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    onOpenTechniqueClick: (String) -> Unit,
    currentHintIndex: Int,
    totalHints: Int,
    onDismissHint: () -> Unit,
    onNextHint: () -> Unit,
    onPrevHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeHint = uiState.activeHint
    val contentScrollState = rememberScrollState()
    val boardTopSpacerHeight by animateDpAsState(
        targetValue = if (activeHint != null) 0.dp else 28.dp,
        animationSpec = tween(320),
        label = "BoardTopSpacer"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        GameTopBar(
            difficulty = uiState.difficulty.name,
            onBackClick = onBackClick,
            onGetDebugDumpClick = onGetDebugDumpClick,
            onCrashlyticsTestCrashClick = onCrashlyticsTestCrashClick,
            onDebugFillCandidatesClick = onDebugFillCandidatesClick,
            onDebugPrepareVictoryClick = onDebugPrepareVictoryClick,
            onOpenTechniquesClick = onOpenTechniquesClick
        )

        GameTimerPill(
            elapsedTimeSeconds = uiState.elapsedTimeSeconds,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(animationSpec = tween(320))
                .padding(16.dp)
                .then(
                    if (activeHint != null) Modifier.verticalScroll(contentScrollState) else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(boardTopSpacerHeight))

            AnimatedContent(
                targetState = uiState.isLoading,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(600)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(600))
                        )
                },
                label = "BoardTransition"
            ) { isLoading ->
                if (isLoading) {
                    SudokuDecodingBoard()
                } else {
                    val displayBoard = uiState.activeHint?.stepBoard ?: uiState.board

                    SudokuBoardView(
                        board = displayBoard,
                        selectedCellId = uiState.selectedCellId,
                        highlightedIds = uiState.highlightedCellIds,
                        sameValueIds = uiState.sameValueCellIds,
                        activeHint = uiState.activeHint,
                        onCellClick = onCellClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (activeHint != null) 12.dp else 30.dp))

            AnimatedVisibility(
                visible = activeHint != null,
                enter = fadeIn(animationSpec = tween(260)) +
                    slideInVertically(
                        animationSpec = tween(320),
                        initialOffsetY = { it / 3 }
                    ),
                exit = fadeOut(animationSpec = tween(180)) +
                    slideOutVertically(
                        animationSpec = tween(220),
                        targetOffsetY = { it / 5 }
                    )
            ) {
                activeHint?.let { hint ->
                    HintOverlayCard(
                        hint = hint,
                        currentStep = currentHintIndex,
                        totalSteps = totalHints,
                        onDismiss = onDismissHint,
                        onNext = onNextHint,
                        onPrev = onPrevHint,
                        onTechniqueClick = { onOpenTechniqueClick(hint.strategyName.toTechniqueId()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (activeHint == null && !uiState.isLoading) {
                GameControls(
                    isNoteMode = uiState.isNoteMode,
                    onToggleNoteMode = onToggleNoteMode,
                    onUndo = onUndo,
                    onHintClick = onHintClick,
                    enabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                NumberPad(
                    onNumberClick = onNumberInput,
                    onDeleteClick = onDeleteInput,
                    completedNumbers = uiState.completedNumbers
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SudokuBoardView(
    board: Board,
    selectedCellId: Int?,
    highlightedIds: Set<Int>,
    sameValueIds: Set<Int>,
    activeHint: SudokuHint?,
    onCellClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(15.dp))
            .background(SudokuPalette.BoardBackground)
            .border(2.dp, SudokuPalette.GridLine, RoundedCornerShape(15.dp))
            .drawWithContent {
                drawContent()

                activeHint?.highlightBoxes?.forEach { boxIndex ->
                    val boxRow = boxIndex / 3
                    val boxCol = boxIndex % 3
                    val cellSize = size.width / 9f
                    val strokeWidth = 3.dp.toPx()

                    drawRect(
                        color = SudokuPalette.CellHintBorder,
                        topLeft = Offset(
                            x = boxCol * 3 * cellSize + strokeWidth / 2,
                            y = boxRow * 3 * cellSize + strokeWidth / 2
                        ),
                        size = Size(
                            width = 3 * cellSize - strokeWidth,
                            height = 3 * cellSize - strokeWidth
                        ),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
    ) {
        board.rows.forEachIndexed { rowIndex, rowCells ->
            Row(modifier = Modifier.weight(1f)) {
                rowCells.forEachIndexed { colIndex, cell ->

                    val rightBorder = if (colIndex == 2 || colIndex == 5) 2.dp else 0.5.dp
                    val bottomBorder = if (rowIndex == 2 || rowIndex == 5) 2.dp else 0.5.dp

                    val isHighlighted = highlightedIds.contains(cell.id)
                    val isSameValue = sameValueIds.contains(cell.id)

                    // NUEVA LÓGICA DE PISTAS:
                    // 1. ¿Es la celda donde se va a poner el número final?
                    val isTargetCell = activeHint?.targetCellIndex == cell.id

                    // 2. ¿Es una celda a la que se le van a borrar notas?
                    val notesToRemoveInThisCell = activeHint?.notesToRemove?.get(cell.id) ?: emptyList()
                    val hasNotesToRemove = notesToRemoveInThisCell.isNotEmpty()

                    // 3. ¿Es una celda que queremos iluminar como "explicación"?
                    val isExplanationCell = activeHint?.highlightCells?.contains(cell.id) == true

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            // todo hacerlo con canvas
                            .drawWithContent {
                                drawContent()
                                // linea derecha
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = rightBorder.toPx()
                                )
                                // linea abajo
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = bottomBorder.toPx()
                                )
                            }
                    ) {
                        CellView(
                            cell = cell,
                            isSelected = cell.id == selectedCellId,
                            isHighlighted = isHighlighted,
                            isSameValue = isSameValue,
                            isHintTarget = isTargetCell || isExplanationCell,
                            isHintElimination = hasNotesToRemove,
                            notesToCrossOut = notesToRemoveInThisCell, // Pasamos las notas a borrar
                            onClick = { onCellClick(cell.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CellView(
    cell: Cell,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isSameValue: Boolean,
    isHintTarget: Boolean, // Ahora esto significa "estoy involucrado en la pista de alguna forma"
    isHintElimination: Boolean,
    notesToCrossOut: List<Int> = emptyList(), // Nueva variable para saber qué notas pintar de rojo
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isHintTarget -> SudokuPalette.CellHint // Fondo doradito para celdas de la pista
        isHintElimination -> SudokuPalette.CellEliminationBg
        cell.isError -> SudokuPalette.CellErrorBg
        isSelected -> SudokuPalette.CellSelected
        isHighlighted -> SudokuPalette.CellHighlight
        isSameValue -> SudokuPalette.CellSelected
        else -> SudokuPalette.CellNormal
    }

    val textColor = when {
        cell.isError && !cell.isGiven -> SudokuPalette.TextError
        cell.isGiven -> SudokuPalette.TextPrimary
        else -> SudokuPalette.TextAccent
    }
    val weight = if (cell.isGiven) FontWeight.Bold else FontWeight.Medium

    val cellModifier = when {
        isHintTarget && cell.value == null && notesToCrossOut.isEmpty() ->
            modifier.border(2.dp, SudokuPalette.CellHintBorder)

        isHintElimination ->
            modifier.border(1.dp, SudokuPalette.CellEliminationBorder)

        else -> modifier
    }

    Box(
        modifier = cellModifier
            .fillMaxSize()
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != null) {
            Text(
                text = cell.value.toString(),
                fontSize = 25.sp,
                fontWeight = weight,
                color = textColor
            )
        } else if (cell.notes.isNotEmpty()) {
            // Le pasamos al grid las notas que están marcadas para morir
            NotesGrid(notes = cell.notes, notesToCrossOut = notesToCrossOut)
        }
    }
}

@Composable
fun NotesGrid(
    notes: Set<Int>,
    // Recibimos la lista de la muerte.
    notesToCrossOut: List<Int> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { col ->
                    val number = row * 3 + col + 1

                    if (notes.contains(number)) {
                        // Si la nota está en la lista de borrado, la pintamos de rojo intenso
                        val isCrossedOut = notesToCrossOut.contains(number)
                        val noteColor = if (isCrossedOut) Color.Red else SudokuPalette.TextSecondary
                        val noteWeight = if (isCrossedOut) FontWeight.Bold else FontWeight.Normal

                        Text(
                            text = number.toString(),
                            fontSize = 8.sp,
                            color = noteColor,
                            fontWeight = noteWeight,
                            lineHeight = 8.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    completedNumbers: Set<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1 al 5
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..5).forEach { number ->
                SudokuButton(
                    text = number.toString(),
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f),
                    enabled = !completedNumbers.contains(number)
                )
            }
        }

        // 9 al X
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (6..9).forEach { number ->
                SudokuButton(
                    text = number.toString(),
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f),
                    enabled = !completedNumbers.contains(number)
                )
            }
            // boton de borrar
            SudokuButton(
                text = "X",
                onClick = onDeleteClick,
                isDestructive = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SudokuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDestructive: Boolean = false
) {
    val containerColor =
        if (isDestructive) SudokuPalette.ButtonDestructive else SudokuPalette.ButtonContainer
    val contentColor =
        if (isDestructive) SudokuPalette.ButtonDestructiveContent else SudokuPalette.ButtonContent

    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameTopBar(
    difficulty: String,
    onBackClick: () -> Unit,
    onGetDebugDumpClick: () -> Unit,
    onCrashlyticsTestCrashClick: () -> Unit,
    onDebugFillCandidatesClick: () -> Unit,
    onDebugPrepareVictoryClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Volver",
            tint = SudokuPalette.TextSecondary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBackClick() }
                .padding(8.dp)
        )

        Text(
            text = difficulty,
            style = MaterialTheme.typography.titleMedium,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = if (BuildConfig.DEBUG) {
                Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = onGetDebugDumpClick
                )
            } else {
                Modifier
            }
                .align(Alignment.Center)
        )

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = SudokuPalette.TextSecondary
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                containerColor = SudokuPalette.HomePanel
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Configuración") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    onClick = { isMenuExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(text = "Técnicas") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onOpenTechniquesClick()
                    }
                )
                if (BuildConfig.DEBUG) {
                    DropdownMenuItem(
                        text = { Text(text = "Agregar candidatos") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDebugFillCandidatesClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Preparar victoria") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDebugPrepareVictoryClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Probar Crashlytics") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onCrashlyticsTestCrashClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameTimerPill(elapsedTimeSeconds: Long, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = SudokuPalette.HomePanel.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, SudokuPalette.GridLine)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                contentDescription = null,
                tint = SudokuPalette.TextAccent,
                modifier = Modifier.size(15.dp)
            )

            Text(
                text = elapsedTimeSeconds.toFormattedTime(),
                style = MaterialTheme.typography.labelLarge,
                color = SudokuPalette.TextAccent,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun String.toTechniqueId(): String {
    return lowercase()
        .replace("-", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString("_")
}

@Composable
fun GameControls(
    modifier: Modifier = Modifier,
    isNoteMode: Boolean,
    onToggleNoteMode: () -> Unit,
    onUndo: () -> Unit,
    onHintClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onUndo,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = SudokuPalette.ButtonContainer,
                contentColor = SudokuPalette.ButtonContent
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Deshacer",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Undo", style = MaterialTheme.typography.labelLarge)
        }

        // notas
        val containerColor =
            if (isNoteMode) SudokuPalette.TextAccent else SudokuPalette.ButtonContainer
        val contentColor =
            if (isNoteMode) SudokuPalette.ScreenBackground else SudokuPalette.TextAccent

        Button(
            onClick = onToggleNoteMode,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modo Notas",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (isNoteMode) "ON" else "OFF",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onHintClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = SudokuPalette.CellHint,
                contentColor = SudokuPalette.CellHintBorder
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Pedir pista",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Hint", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF161823
)
@Composable
fun GameScreenPreview(@PreviewParameter(GamePreviewProvider::class) uiState: GameUiState) {
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
        onOpenTechniquesClick = {},
        onOpenTechniqueClick = {},
        currentHintIndex = 0,
        totalHints = uiState.activeHints.size,
        onDismissHint = {},
        onNextHint = {},
        onPrevHint = {},
        onUndo = {}
    )
}
