package ropa.miragaya.sudokupremium.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
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
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.game.component.GameWonDialog
import ropa.miragaya.sudokupremium.ui.game.component.SudokuDecodingBoard
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

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
            onStartNewGame = { viewModel.startNewGame(Difficulty.MEDIUM) } // todo seleccionar dificultad
        )
    }

    GameContent(
        uiState = uiState,
        onCellClick = viewModel::onCellClicked,
        onNumberInput = viewModel::onNumberInput,
        onDeleteInput = viewModel::onDeleteInput,
        onToggleNoteMode = viewModel::toggleNoteMode,
        onUndo = viewModel::onUndo,
        onBackClick = onBackClick,
        modifier = modifier
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient),
    ) {

        GameTopBar(
            difficulty = uiState.difficulty.name,
            elapsedTimeSeconds = uiState.elapsedTimeSeconds,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                    SudokuBoardView(
                        board = uiState.board,
                        selectedCellId = uiState.selectedCellId,
                        highlightedIds = uiState.highlightedCellIds,
                        sameValueIds = uiState.sameValueCellIds,
                        onCellClick = onCellClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GameControls(
                isNoteMode = uiState.isNoteMode,
                onToggleNoteMode = onToggleNoteMode,
                onUndo = onUndo
            )

            Spacer(modifier = Modifier.height(16.dp))

            NumberPad(
                onNumberClick = onNumberInput,
                onDeleteClick = onDeleteInput
            )
        }
    }
}

@Composable
fun SudokuBoardView(
    board: Board,
    selectedCellId: Int?,
    highlightedIds: Set<Int>,
    sameValueIds: Set<Int>,
    onCellClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(15.dp))
            .background(SudokuPalette.BoardBackground)
            .border(2.dp, SudokuPalette.GridLine, RoundedCornerShape(15.dp))
    ) {
        board.rows.forEachIndexed { rowIndex, rowCells ->
            Row(modifier = Modifier.weight(1f)) {
                rowCells.forEachIndexed { colIndex, cell ->

                    val rightBorder = if (colIndex == 2 || colIndex == 5) 2.dp else 0.5.dp
                    val bottomBorder = if (rowIndex == 2 || rowIndex == 5) 2.dp else 0.5.dp

                    val isHighlighted = highlightedIds.contains(cell.id)
                    val isSameValue = sameValueIds.contains(cell.id)

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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val bgColor = when {
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

    Box(
        modifier = modifier
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
            NotesGrid(notes = cell.notes)
        }
    }
}

@Composable
fun NotesGrid(notes: Set<Int>) {
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
                        Text(
                            text = number.toString(),
                            fontSize = 8.sp,
                            color = SudokuPalette.TextSecondary,
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
                    modifier = Modifier.weight(1f)
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
                    modifier = Modifier.weight(1f)
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

@Composable
fun GameTopBar(
    difficulty: String,
    elapsedTimeSeconds: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Volver",
            tint = SudokuPalette.TextSecondary,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(8.dp)
        )

        Text(
            text = difficulty,
            style = MaterialTheme.typography.titleMedium,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                contentDescription = null,
                tint = SudokuPalette.TextAccent,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = elapsedTimeSeconds.toFormattedTime(),
                style = MaterialTheme.typography.titleMedium,
                color = SudokuPalette.TextAccent,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun GameControls(
    isNoteMode: Boolean,
    onToggleNoteMode: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onUndo,
            colors = ButtonDefaults.buttonColors(
                containerColor = SudokuPalette.ButtonContainer,
                contentColor = SudokuPalette.ButtonContent
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
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
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
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
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF161823
)
@Composable
fun GameScreenPreview(
    @PreviewParameter(GamePreviewProvider::class) uiState: GameUiState
) {
    GameContent(
        uiState = uiState,
        onCellClick = {},
        onNumberInput = {},
        onDeleteInput = {},
        onBackClick = {},
        onToggleNoteMode = {},
        onUndo = {}
    )
}

