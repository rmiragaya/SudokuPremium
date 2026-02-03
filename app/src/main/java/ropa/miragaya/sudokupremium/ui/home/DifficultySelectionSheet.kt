package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectionSheet(
    onDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SudokuPalette.BoardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selecciona Dificultad",
                style = MaterialTheme.typography.headlineSmall,
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Difficulty.entries.forEach { difficulty ->
                DifficultyItem(
                    difficulty = difficulty,
                    onClick = { onDifficultySelected(difficulty) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DifficultyItem(
    difficulty: Difficulty,
    onClick: () -> Unit
) {
    // Configuracion visual segun dificultad
    val (stars, description) = when (difficulty) {
        Difficulty.EASY -> 1 to "Ideal para empezar"
        Difficulty.MEDIUM -> 2 to "Para mentes ágiles"
        Difficulty.HARD -> 3 to "Desafío real"
        Difficulty.EXPERT -> 4 to "Solo para genios"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SudokuPalette.ButtonContainer)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = difficulty.name,
                style = MaterialTheme.typography.titleMedium,
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SudokuPalette.TextSecondary
            )
        }

        Row {
            repeat(stars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = SudokuPalette.TextAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}