package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.concurrent.TimeUnit
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
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
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VictoryKonfetti(
                modifier = Modifier
                    .matchParentSize()
            )

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
}

@Composable
private fun VictoryKonfetti(modifier: Modifier = Modifier) {
    val parties = remember {
        listOf(
            victoryParty(position = Position.Relative(0.12, 0.08), angle = 35),
            victoryParty(position = Position.Relative(0.88, 0.08), angle = 145)
        )
    }

    KonfettiView(
        modifier = modifier,
        parties = parties
    )
}

private fun victoryParty(position: Position, angle: Int): Party {
    return Party(
        angle = angle,
        spread = 56,
        speed = 8f,
        maxSpeed = 22f,
        damping = 0.88f,
        timeToLive = 1900L,
        colors = listOf(
            0xFF64B5F6.toInt(),
            0xFFFFD54F.toInt(),
            0xFF6ED6A5.toInt(),
            0xFFFFFFFF.toInt()
        ),
        position = position,
        emitter = Emitter(duration = 850, TimeUnit.MILLISECONDS).max(70)
    )
}

@Composable
private fun VictoryGlyph() {
    val transition = rememberInfiniteTransition(label = "VictoryGlyph")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "VictoryGlow"
    )

    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer(alpha = glowAlpha),
            shape = RoundedCornerShape(28.dp),
            color = SudokuPalette.TextAccent,
            content = {}
        )

        Surface(
            modifier = Modifier.size(78.dp),
            shape = RoundedCornerShape(24.dp),
            color = SudokuPalette.HomeBadgeBackground,
            border = BorderStroke(1.dp, SudokuPalette.TextAccent.copy(alpha = 0.42f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(3) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) { col ->
                            val isSolvedPath = row == col || row + col == 2
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isSolvedPath) {
                                            SudokuPalette.TextAccent
                                        } else {
                                            SudokuPalette.GridLine
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
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
