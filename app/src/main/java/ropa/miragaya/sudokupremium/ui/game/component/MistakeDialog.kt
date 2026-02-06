package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun MistakeDialog(
    mistakeCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = SudokuPalette.TextAccent,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Algo no cuadra \uD83E\uDD14",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val message = if (mistakeCount == 1) {
                    "Encontramos 1 número que no coincide con la solución final."
                } else {
                    "Encontramos $mistakeCount números que no coinciden con la solución final."
                }

                Text(
                    text = "$message\nNo podemos calcular una pista lógica hasta que el tablero sea válido.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                val question = if (mistakeCount == 1) {
                    "¿Querés ver dónde está?"
                } else {
                    "¿Querés ver dónde están?"
                }

                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SudokuPalette.TextPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.ButtonContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = SudokuPalette.TextSecondary)
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.TextAccent),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ok", color = SudokuPalette.BoardBackground)
                    }
                }
            }
        }
    }
}