package ropa.miragaya.sudokupremium.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ropa.miragaya.sudokupremium.data.SAMPLE_PUZZLE
import ropa.miragaya.sudokupremium.domain.factory.BoardFactory
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    // 1. Cargamos el tablero (Simulamos un ViewModel por ahora)
    val board = remember { BoardFactory.fromString(SAMPLE_PUZZLE) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        SudokuBoardView(board)
    }
}

@Composable
fun SudokuBoardView(board: Board) {
    // Dibujamos el contenedor del tablero con un borde grueso externo
    Column(
        modifier = Modifier
            .padding(16.dp)
            .border(2.dp, Color.Black)
    ) {
        // Iteramos las filas pre-calculadas de tu Board
        board.rows.forEach { rowCells ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowCells.forEach { cell ->
                    CellView(
                        cell = cell,
                        modifier = Modifier
                            .weight(1f)      // Clave: Divide el ancho equitativamente
                            .aspectRatio(1f) // Clave: Mantiene la celda cuadrada
                    )
                }
            }
        }
    }
}

@Composable
fun CellView(
    cell: Cell,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(0.5.dp, Color.Gray), // Borde finito para cada celda
        contentAlignment = Alignment.Center
    ) {
        // Solo mostramos el texto si value no es null
        if (cell.value != null) {
            Text(
                text = cell.value.toString(),
                fontSize = 20.sp,
                // Si es pista (Given), va en negrita. Si no, normal.
                fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen()
}