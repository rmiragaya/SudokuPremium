package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun HintDialog(
    hint: SudokuHint,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SudokuPalette.BoardBackground),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "💡 ${hint.strategyName}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SudokuPalette.TextAccent,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "📍 Fila ${hint.row + 1}, Columna ${hint.col + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = SudokuPalette.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(SudokuPalette.GridLine.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = hint.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.TextAccent)
                ) {
                    Text("¡Ok!", color = SudokuPalette.BoardBackground)
                }
            }
        }
    }
}