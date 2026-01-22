package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameWonDialog(
    elapsedTimeSeconds: Long,
    onStartNewGame: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¡Felicitaciones!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SudokuPalette.TextAccent
            )
        },
        text = {
            Column {
                Text(
                    text = "Has completado el Sudoku correctamente.",
                    color = SudokuPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tiempo: ${elapsedTimeSeconds.toFormattedTime()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SudokuPalette.TextPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onStartNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudokuPalette.TextAccent,
                    contentColor = SudokuPalette.ScreenBackground
                )
            ) {
                Text("Nueva Partida")
            }
        },
        containerColor = SudokuPalette.BoardBackground,
        titleContentColor = SudokuPalette.TextPrimary,
        textContentColor = SudokuPalette.TextPrimary
    )
}