package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SudokuPalette.BoardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HintHeader(
                strategyName = hint.strategyName,
                currentStep = currentStep,
                totalSteps = totalSteps,
                onPrev = onPrev,
                onNext = onNext,
                onDismiss = onDismiss
            )

            HintMetaRow(hint = hint)

            Text(
                text = hint.description,
                style = MaterialTheme.typography.bodyMedium,
                color = SudokuPalette.TextPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 142.dp)
                    .verticalScroll(rememberScrollState())
            )

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.TextAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = "Entendido",
                    color = SudokuPalette.BoardBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HintHeader(
    strategyName: String,
    currentStep: Int,
    totalSteps: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SudokuPalette.CellHint,
            border = BorderStroke(1.dp, SudokuPalette.CellHintBorder)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = SudokuPalette.CellHintBorder,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = strategyName,
                style = MaterialTheme.typography.titleMedium,
                color = SudokuPalette.CellHintBorder,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            IconButton(
                onClick = onPrev,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Anterior",
                    tint = SudokuPalette.TextAccent
                )
            }
            IconButton(
                onClick = onNext,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Siguiente",
                    tint = SudokuPalette.TextAccent
                )
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = SudokuPalette.TextSecondary
            )
        }
    }
}

@Composable
private fun HintMetaRow(hint: SudokuHint) {
    val actionLabel = hint.actionLabel()
    val scopeLabel = hint.scopeLabel()

    if (actionLabel == null && scopeLabel == null) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actionLabel?.let {
            HintChip(
                text = it,
                modifier = Modifier.weight(1f)
            )
        }
        scopeLabel?.let {
            HintChip(
                text = it,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HintChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = SudokuPalette.ButtonContainer,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SudokuPalette.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun SudokuHint.actionLabel(): String? {
    valueToSet?.let { return "Colocar $it" }

    val removedNotes = notesToRemove.values
        .flatten()
        .distinct()
        .sorted()

    return if (removedNotes.isNotEmpty()) {
        "Quitar ${formatNumbers(removedNotes)}"
    } else {
        null
    }
}

private fun SudokuHint.scopeLabel(): String? {
    val affectedCells: Int
    val suffix: String

    when {
        notesToRemove.isNotEmpty() -> {
            affectedCells = notesToRemove.keys.size
            suffix = "a limpiar"
        }

        highlightCells.isNotEmpty() -> {
            affectedCells = highlightCells.size
            suffix = "del patrón"
        }

        targetCellIndex != null -> {
            affectedCells = 1
            suffix = "objetivo"
        }

        else -> return null
    }

    val noun = if (affectedCells == 1) "casilla" else "casillas"
    return "$affectedCells $noun $suffix"
}

private fun formatNumbers(numbers: List<Int>): String {
    return when (numbers.size) {
        0 -> ""
        1 -> numbers.first().toString()
        2 -> "${numbers[0]} y ${numbers[1]}"
        else -> numbers.dropLast(1).joinToString(", ") + " y " + numbers.last()
    }
}
