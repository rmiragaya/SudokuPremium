package ropa.miragaya.sudokupremium.ui.home

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

private val DifficultyBodyFont = FontFamily.SansSerif

@Composable
fun DifficultySelectionSheet(onDismiss: () -> Unit, onDifficultySelected: (Difficulty) -> Unit) {
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    var isClosing by remember { mutableStateOf(false) }

    fun closeSheet(afterClose: (() -> Unit)? = null) {
        if (isClosing) return

        isClosing = true
        isVisible = false
        scope.launch {
            delay(DIFFICULTY_SHEET_EXIT_MILLIS.toLong())
            afterClose?.invoke() ?: onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    BackHandler(enabled = isVisible && !isClosing) {
        closeSheet()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(DIFFICULTY_SHEET_SCRIM_ENTER_MILLIS)),
            exit = fadeOut(animationSpec = tween(DIFFICULTY_SHEET_SCRIM_EXIT_MILLIS))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.48f))
                    .clickable { closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                animationSpec = tween(DIFFICULTY_SHEET_ENTER_MILLIS),
                initialOffsetY = { it }
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = DIFFICULTY_SHEET_CONTENT_FADE_MILLIS,
                    delayMillis = DIFFICULTY_SHEET_CONTENT_FADE_DELAY_MILLIS
                )
            ),
            exit = slideOutVertically(
                animationSpec = tween(DIFFICULTY_SHEET_EXIT_MILLIS),
                targetOffsetY = { it }
            ) + fadeOut(animationSpec = tween(DIFFICULTY_SHEET_CONTENT_EXIT_FADE_MILLIS))
        ) {
            DifficultySheetPanel(
                onDifficultySelected = { difficulty ->
                    onDifficultySelected(difficulty)
                }
            )
        }
    }
}

@Composable
private fun DifficultySheetPanel(onDifficultySelected: (Difficulty) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = SudokuPalette.HomePanel,
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
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
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, bottom = 6.dp),
                shape = RoundedCornerShape(999.dp),
                color = SudokuPalette.HomeBorder
            ) {
                Spacer(modifier = Modifier.size(width = 42.dp, height = 4.dp))
            }

            Column(
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.difficulty_sheet_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = DifficultyBodyFont,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.difficulty_sheet_description),
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
                        text = stringResource(spec.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = DifficultyBodyFont,
                        color = SudokuPalette.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    DifficultyPill(
                        text = stringResource(spec.pillRes),
                        accent = spec.accent
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = stringResource(spec.descriptionRes),
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
    @param:StringRes val titleRes: Int,
    @param:StringRes val pillRes: Int,
    @param:StringRes val descriptionRes: Int,
    val level: Int,
    val accent: Color,
    val icon: ImageVector
)

private const val DIFFICULTY_SHEET_ENTER_MILLIS = 680
private const val DIFFICULTY_SHEET_EXIT_MILLIS = 420
private const val DIFFICULTY_SHEET_SCRIM_ENTER_MILLIS = 520
private const val DIFFICULTY_SHEET_SCRIM_EXIT_MILLIS = 320
private const val DIFFICULTY_SHEET_CONTENT_FADE_MILLIS = 520
private const val DIFFICULTY_SHEET_CONTENT_FADE_DELAY_MILLIS = 90
private const val DIFFICULTY_SHEET_CONTENT_EXIT_FADE_MILLIS = 260

private fun Difficulty.spec(): DifficultySpec {
    return when (this) {
        Difficulty.EASY -> DifficultySpec(
            titleRes = R.string.difficulty_easy_title,
            pillRes = R.string.difficulty_easy_pill,
            descriptionRes = R.string.difficulty_easy_description,
            level = 1,
            accent = Color(0xFF6ED6A5),
            icon = Icons.Default.School
        )

        Difficulty.MEDIUM -> DifficultySpec(
            titleRes = R.string.difficulty_medium_title,
            pillRes = R.string.difficulty_medium_pill,
            descriptionRes = R.string.difficulty_medium_description,
            level = 2,
            accent = SudokuPalette.TextAccent,
            icon = Icons.Default.Psychology
        )

        Difficulty.HARD -> DifficultySpec(
            titleRes = R.string.difficulty_hard_title,
            pillRes = R.string.difficulty_hard_pill,
            descriptionRes = R.string.difficulty_hard_description,
            level = 3,
            accent = Color(0xFFFFB86B),
            icon = Icons.Default.Bolt
        )

        Difficulty.EXPERT -> DifficultySpec(
            titleRes = R.string.difficulty_expert_title,
            pillRes = R.string.difficulty_expert_pill,
            descriptionRes = R.string.difficulty_expert_description,
            level = 4,
            accent = Color(0xFFFF6B8A),
            icon = Icons.Default.SportsScore
        )
    }
}
