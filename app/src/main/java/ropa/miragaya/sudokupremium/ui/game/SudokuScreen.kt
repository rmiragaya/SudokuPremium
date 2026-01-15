package ropa.miragaya.sudokupremium.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TABLERO
        SudokuBoardView(
            board = uiState.board,
            selectedCellId = uiState.selectedCellId,
            onCellClick = viewModel::onCellClicked
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TECLADO (Nuevo componente)
        NumberPad(onNumberClick = viewModel::onNumberInput)
    }
}

@Composable
fun SudokuBoardView(
    board: Board,
    selectedCellId: Int?,
    onCellClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .aspectRatio(1f) // Cuadrado perfecto
            .border(2.dp, Color.Black) // Borde exterior grueso
    ) {
        board.rows.forEachIndexed { rowIndex, rowCells ->
            Row(modifier = Modifier.weight(1f)) {
                rowCells.forEachIndexed { colIndex, cell ->
                    // LÓGICA VISUAL DE BLOQUES 3x3
                    // Si estamos en la columna 2 o 5, dibujamos una linea derecha más gruesa
                    val rightBorder = if (colIndex == 2 || colIndex == 5) 2.dp else 0.5.dp
                    // Si estamos en la fila 2 o 5, dibujamos linea abajo más gruesa
                    val bottomBorder = if (rowIndex == 2 || rowIndex == 5) 2.dp else 0.5.dp

                    // Usamos un Box contenedor para manejar los bordes "extra" del bloque 3x3
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            // Dibujamos bordes selectivos.
                            // Nota: Esto es un truco simple. Para perfección absoluta se usa Canvas,
                            // pero esto cumple el objetivo visualmente.
                            .drawBehind {
                                // Dibujar linea derecha
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = rightBorder.toPx()
                                )
                                // Dibujar linea abajo
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = bottomBorder.toPx()
                                )
                            }
                    ) {
                        CellView(
                            cell = cell,
                            isSelected = cell.id == selectedCellId,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Colores dinámicos según estado
    val bgColor = if (isSelected) Color(0xFFBBDEFB) else Color.Transparent
    val textColor = if (cell.isGiven) Color.Black else Color(0xFF1565C0) // Azul para input usuario
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
                fontSize = 20.sp,
                fontWeight = weight,
                color = textColor
            )
        }
    }
}

@Composable
fun NumberPad(onNumberClick: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..9).forEach { number ->
            Surface(
                onClick = { onNumberClick(number) },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}