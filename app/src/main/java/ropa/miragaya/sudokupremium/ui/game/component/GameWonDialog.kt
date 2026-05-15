package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.ui.component.MentorButton
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameWonDialog(
    difficulty: String,
    elapsedTimeSeconds: Long,
    hintsUsed: Int,
    onStartNewGame: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SudokuPalette.HomePanel,
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
            tonalElevation = 10.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VictoryGlyph()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.victory_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.victory_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    VictoryStatRow(
                        label = stringResource(R.string.victory_time),
                        value = elapsedTimeSeconds.toFormattedTime()
                    )
                    VictoryStatRow(
                        label = stringResource(R.string.victory_difficulty),
                        value = difficulty.toDifficultyLabel()
                    )
                    VictoryStatRow(
                        label = stringResource(R.string.victory_hints),
                        value = hintsUsed.toHintsLabel()
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                MentorButton(
                    text = stringResource(R.string.home_new_game),
                    onClick = onStartNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun VictoryGlyph() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.victory_sudoku_logic)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(124.dp)
    )
}

@Composable
private fun VictoryStatRow(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SudokuPalette.HomeBadgeBackground,
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = SudokuPalette.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                color = SudokuPalette.TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun String.toDifficultyLabel(): String {
    return when (this) {
        "EASY" -> stringResource(R.string.difficulty_easy_title)
        "MEDIUM" -> stringResource(R.string.difficulty_medium_title)
        "HARD" -> stringResource(R.string.difficulty_hard_title)
        "EXPERT" -> stringResource(R.string.difficulty_expert_title)
        else -> this.lowercase().replaceFirstChar { it.titlecase() }
    }
}

@Composable
private fun Int.toHintsLabel(): String {
    return when (this) {
        0 -> stringResource(R.string.victory_no_hints)
        1 -> stringResource(R.string.victory_one_hint)
        else -> stringResource(R.string.victory_many_hints, this)
    }
}
