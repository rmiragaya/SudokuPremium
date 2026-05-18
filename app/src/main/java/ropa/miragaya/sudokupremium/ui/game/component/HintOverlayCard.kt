package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.ui.component.MentorButton
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun HintOverlayCard(
    hint: SudokuHint,
    currentStep: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onDismiss: () -> Unit,
    onTechniqueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SudokuPalette.BoardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HintHeader(
                strategyName = hint.strategyName,
                currentStep = currentStep,
                totalSteps = totalSteps,
                onPrev = onPrev,
                onNext = onNext,
                onDismiss = onDismiss,
                onTechniqueClick = onTechniqueClick
            )

            HintMetaRow(hint = hint)

            Text(
                text = hint.description,
                style = MaterialTheme.typography.bodySmall,
                color = SudokuPalette.TextPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 112.dp)
                    .verticalScroll(rememberScrollState())
            )

            MentorButton(
                text = stringResource(R.string.action_understood),
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth(),
                height = 40.dp
            )
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
    onDismiss: () -> Unit,
    onTechniqueClick: () -> Unit
) {
    val canGoPrev = currentStep > 0
    val canGoNext = currentStep < totalSteps - 1

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    .padding(7.dp)
                    .size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = strategyName,
                style = MaterialTheme.typography.titleSmall,
                color = SudokuPalette.CellHintBorder,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (totalSteps > 1) {
                    Text(
                        text = stringResource(R.string.hint_step_count, currentStep + 1, totalSteps),
                        style = MaterialTheme.typography.labelSmall,
                        color = SudokuPalette.TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = SudokuPalette.TextAccent.copy(alpha = 0.12f),
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .clickable(onClick = onTechniqueClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = SudokuPalette.TextAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.hint_view_technique),
                            style = MaterialTheme.typography.labelSmall,
                            color = SudokuPalette.TextAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (totalSteps > 1) {
            IconButton(
                onClick = onPrev,
                enabled = canGoPrev,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_previous),
                    tint = if (canGoPrev) SudokuPalette.TextAccent else SudokuPalette.TextSecondary.copy(alpha = 0.35f)
                )
            }
            IconButton(
                onClick = onNext,
                enabled = canGoNext,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.action_next),
                    tint = if (canGoNext) SudokuPalette.TextAccent else SudokuPalette.TextSecondary.copy(alpha = 0.35f)
                )
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(R.string.action_close),
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
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
private fun HintChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = SudokuPalette.ButtonContainer,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = SudokuPalette.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SudokuHint.actionLabel(): String? {
    valueToSet?.let { return stringResource(R.string.hint_action_place, it) }

    val removedNotes = notesToRemove.values
        .flatten()
        .distinct()
        .sorted()

    return if (removedNotes.isNotEmpty()) {
        stringResource(R.string.hint_action_remove, formatNumbers(removedNotes))
    } else {
        null
    }
}

@Composable
private fun SudokuHint.scopeLabel(): String? {
    val affectedCells: Int

    when {
        notesToRemove.isNotEmpty() -> {
            affectedCells = notesToRemove.keys.size
            return if (affectedCells == 1) {
                stringResource(R.string.hint_scope_remove_one)
            } else {
                stringResource(R.string.hint_scope_remove_many, affectedCells)
            }
        }

        highlightCells.isNotEmpty() -> {
            affectedCells = highlightCells.size
            return if (affectedCells == 1) {
                stringResource(R.string.hint_scope_highlight_one)
            } else {
                stringResource(R.string.hint_scope_highlight_many, affectedCells)
            }
        }

        targetCellIndex != null -> {
            return stringResource(R.string.hint_scope_target)
        }

        else -> return null
    }
}

private fun formatNumbers(numbers: List<Int>): String {
    return when (numbers.size) {
        0 -> ""
        1 -> numbers.first().toString()
        2 -> "${numbers[0]} y ${numbers[1]}"
        else -> numbers.dropLast(1).joinToString(", ") + " y " + numbers.last()
    }
}
