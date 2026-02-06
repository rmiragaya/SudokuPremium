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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun HintOverlayCard(
    hint: SudokuHint,
    currentStep: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = SudokuPalette.BoardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (totalSteps > 1) {
                    IconButton(onClick = onPrev) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Anterior", tint = SudokuPalette.TextAccent)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "💡 ${hint.strategyName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = SudokuPalette.CellHintBorder,
                        fontWeight = FontWeight.Bold
                    )
                    if (totalSteps > 1) {
                        Text(
                            text = "${currentStep + 1} de $totalSteps",
                            style = MaterialTheme.typography.labelSmall,
                            color = SudokuPalette.TextSecondary
                        )
                    }
                }

                if (totalSteps > 1) {
                    IconButton(onClick = onNext) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Siguiente", tint = SudokuPalette.TextAccent)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = hint.description,
                style = MaterialTheme.typography.bodyLarge,
                color = SudokuPalette.TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.TextAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¡Entendido!",
                    color = SudokuPalette.BoardBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}