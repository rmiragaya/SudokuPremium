package ropa.miragaya.sudokupremium.ui.game

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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

    GameContent(
        uiState = uiState,
        onCellClick = viewModel::onCellClicked,
        onNumberInput = viewModel::onNumberInput,
        onDeleteInput = viewModel::onDeleteInput,
        onBackClick = { /* TODO: Navegar atrás */ },
        modifier = modifier
    )
}

@Composable
fun GameContent(
    uiState: GameUiState,
    onCellClick: (Int) -> Unit,
    onNumberInput: (Int) -> Unit,
    onDeleteInput: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SudokuPalette.ScreenBackground)
    ) {

        // 1. TOP BAR
        GameTopBar(
            difficulty = uiState.difficulty.name, // O una función para traducirlo bonito
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
            // TABLERO
            SudokuBoardView(
                board = uiState.board,
                selectedCellId = uiState.selectedCellId,
                highlightedIds = uiState.highlightedCellIds,
                sameValueIds = uiState.sameValueCellIds,
                onCellClick = onCellClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TECLADO
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
                                // Linea derecha
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = rightBorder.toPx()
                                )
                                // Linea abajo
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
            // Botón X (Borrar)
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

        // 2. Dificultad (Centro)
        Text(
            text = difficulty,
            style = MaterialTheme.typography.titleMedium,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        // 3. Timer (Derecha)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icono de reloj opcional
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_recent_history), // O un icono de reloj mejor si tenés
                contentDescription = null,
                tint = SudokuPalette.TextAccent,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = elapsedTimeSeconds.toFormattedTime(), // <--- USAMOS LA EXTENSION
                style = MaterialTheme.typography.titleMedium,
                color = SudokuPalette.TextAccent, // Azulito para destacar
                fontFamily = FontFamily.Monospace // Para que los números no bailen cuando cambian
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
){
    GameContent(
        uiState = uiState,
        onCellClick = {},
        onNumberInput = {},
        onDeleteInput = {},
        onBackClick = {})
}

