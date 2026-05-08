package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

private val DifficultyBodyFont = FontFamily.SansSerif

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectionSheet(onDismiss: () -> Unit, onDifficultySelected: (Difficulty) -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SudokuPalette.HomePanel,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                shape = RoundedCornerShape(999.dp),
                color = SudokuPalette.HomeBorder
            ) {
                Spacer(modifier = Modifier.size(width = 42.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "Elegí dificultad",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = DifficultyBodyFont,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Cada nivel cambia el tipo de técnica que vas a practicar.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = DifficultyBodyFont,
                    color = SudokuPalette.TextSecondary
                )
            }

            Difficulty.entries.forEach { difficulty ->
                DifficultyItem(
                    difficulty = difficulty,
                    onClick = { onDifficultySelected(difficulty) }
                )
            }
        }
    }
}

@Composable
fun DifficultyItem(difficulty: Difficulty, onClick: () -> Unit) {
    val spec = difficulty.spec()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = SudokuPalette.ButtonContainer,
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = spec.accent.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, spec.accent.copy(alpha = 0.44f))
            ) {
                Icon(
                    imageVector = spec.icon,
                    contentDescription = null,
                    tint = spec.accent,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = spec.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = DifficultyBodyFont,
                        color = SudokuPalette.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    DifficultyPill(
                        text = spec.pill,
                        accent = spec.accent
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = spec.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = DifficultyBodyFont,
                    color = SudokuPalette.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(spec.level) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = spec.accent,
                        modifier = Modifier.size(7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyPill(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = DifficultyBodyFont,
            color = accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class DifficultySpec(
    val title: String,
    val pill: String,
    val description: String,
    val level: Int,
    val accent: Color,
    val icon: ImageVector
)

private fun Difficulty.spec(): DifficultySpec {
    return when (this) {
        Difficulty.EASY -> DifficultySpec(
            title = "Easy",
            pill = "Calma",
            description = "Ideal para entrar en ritmo y practicar singles.",
            level = 1,
            accent = Color(0xFF6ED6A5),
            icon = Icons.Default.School
        )

        Difficulty.MEDIUM -> DifficultySpec(
            title = "Medium",
            pill = "Técnica",
            description = "Pares, intersecciones y lectura de candidatos.",
            level = 2,
            accent = SudokuPalette.TextAccent,
            icon = Icons.Default.Psychology
        )

        Difficulty.HARD -> DifficultySpec(
            title = "Hard",
            pill = "Desafío",
            description = "Patrones encadenados y tableros más cerrados.",
            level = 3,
            accent = Color(0xFFFFB86B),
            icon = Icons.Default.Bolt
        )

        Difficulty.EXPERT -> DifficultySpec(
            title = "Expert",
            pill = "Avanzada",
            description = "Técnicas exigentes y sesiones con más análisis.",
            level = 4,
            accent = Color(0xFFFF6B8A),
            icon = Icons.Default.SportsScore
        )
    }
}
