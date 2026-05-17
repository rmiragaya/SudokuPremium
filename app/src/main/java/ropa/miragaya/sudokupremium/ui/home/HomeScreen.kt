package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

private val HomeBodyFont = FontFamily.SansSerif
private const val DIFFICULTY_SHEET_RESET_AFTER_NAVIGATION_MILLIS = 1_100L
private const val HOME_LOGO_VISIBLE_LEFT = 0.16f
private const val HOME_LOGO_VISIBLE_TOP = 0.16f
private const val HOME_LOGO_VISIBLE_RIGHT = 0.84f
private const val HOME_LOGO_VISIBLE_BOTTOM = 0.86f
private val HomeGlowCyan = Color(0xFF043E7C)
private val HomeGlowBlue = Color(0xFF0047C8)
private val HomeGlowIndigo = Color(0xFF1C25B8)
private val HomeGlowPurple = Color(0xFF500D91)

@Composable
fun HomeScreen(
    onNewGameClick: (Difficulty) -> Unit,
    onContinueClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val hasSavedGame by viewModel.hasSavedGame.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showDifficultySheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreenContent(
            hasSavedGame = hasSavedGame,
            onNewGameClick = { showDifficultySheet = true },
            onContinueClick = onContinueClick
        )

        if (showDifficultySheet) {
            DifficultySelectionSheet(
                onDismiss = { showDifficultySheet = false },
                onDifficultySelected = { difficulty ->
                    onNewGameClick(difficulty)
                    scope.launch {
                        delay(DIFFICULTY_SHEET_RESET_AFTER_NAVIGATION_MILLIS)
                        showDifficultySheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreenContent(hasSavedGame: Boolean, onNewGameClick: () -> Unit, onContinueClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SudokuPalette.Night)
    ) {
        Image(
            painter = painterResource(R.drawable.home_bkg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 54.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            HomeLogo(modifier = Modifier.size(118.dp))

            Spacer(modifier = Modifier.height(30.dp))

            HomeHeader()

            Spacer(modifier = Modifier.height(26.dp))

            HomeBadge()

            Spacer(modifier = Modifier.height(34.dp))

            HomeActionButton(
                text = stringResource(R.string.home_new_game),
                onClick = onNewGameClick,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (hasSavedGame) {
                Spacer(modifier = Modifier.height(16.dp))

                HomeActionButton(
                    text = stringResource(R.string.home_continue),
                    onClick = onContinueClick,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HomeLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.sudoku_mentor_icon_dark_transparent_png),
            contentDescription = stringResource(R.string.home_title),
            modifier = Modifier.fillMaxSize()
        )
        HomeLogoGlow(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun HomeLogoGlow(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "home-logo-glow")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing)
        ),
        label = "home-logo-glow-progress"
    )

    Canvas(modifier = modifier) {
        val left = size.width * HOME_LOGO_VISIBLE_LEFT
        val top = size.height * HOME_LOGO_VISIBLE_TOP
        val right = size.width * HOME_LOGO_VISIBLE_RIGHT
        val bottom = size.height * HOME_LOGO_VISIBLE_BOTTOM
        val cellWidth = (right - left) / 3f
        val cellHeight = (bottom - top) / 3f

        fun pulse(offset: Float): Float {
            val phase = (progress + offset) % 1f
            if (phase > 0.58f) return 0f
            return sin((phase / 0.58f) * PI).toFloat()
        }

        fun glowSegment(
            start: androidx.compose.ui.geometry.Offset,
            end: androidx.compose.ui.geometry.Offset,
            color: Color,
            offset: Float
        ) {
            val alpha = pulse(offset)
            if (alpha <= 0f) return

            drawLine(
                color = color.copy(alpha = 0.18f * alpha),
                start = start,
                end = end,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = color.copy(alpha = 0.36f * alpha),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.10f * alpha),
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        fun glowArc(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            startAngle: Float,
            sweepAngle: Float,
            color: Color,
            offset: Float
        ) {
            val alpha = pulse(offset)
            if (alpha <= 0f) return

            drawArc(
                color = color.copy(alpha = 0.18f * alpha),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color.copy(alpha = 0.36f * alpha),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        val arcSize = (right - left) * 0.22f

        glowArc(
            left = left,
            top = top,
            right = left + arcSize,
            bottom = top + arcSize,
            startAngle = 180f,
            sweepAngle = 88f,
            color = HomeGlowCyan,
            offset = 0.86f
        )
        glowArc(
            left = right - arcSize,
            top = top,
            right = right,
            bottom = top + arcSize,
            startAngle = 270f,
            sweepAngle = 88f,
            color = HomeGlowPurple,
            offset = 0.06f
        )
        glowArc(
            left = left,
            top = bottom - arcSize,
            right = left + arcSize,
            bottom = bottom,
            startAngle = 90f,
            sweepAngle = 88f,
            color = HomeGlowCyan,
            offset = 0.44f
        )
        glowArc(
            left = right - arcSize,
            top = bottom - arcSize,
            right = right,
            bottom = bottom,
            startAngle = 0f,
            sweepAngle = 88f,
            color = HomeGlowIndigo,
            offset = 0.22f
        )

        glowSegment(
            start = androidx.compose.ui.geometry.Offset(left + cellWidth * 0.85f, top),
            end = androidx.compose.ui.geometry.Offset(right - cellWidth * 0.18f, top),
            color = HomeGlowPurple,
            offset = 0.00f
        )
        glowSegment(
            start = androidx.compose.ui.geometry.Offset(right, top + cellHeight * 0.10f),
            end = androidx.compose.ui.geometry.Offset(right, top + cellHeight * 1.42f),
            color = HomeGlowPurple,
            offset = 0.18f
        )
        glowSegment(
            start = androidx.compose.ui.geometry.Offset(left, top + cellHeight * 1.78f),
            end = androidx.compose.ui.geometry.Offset(left, bottom - cellHeight * 0.12f),
            color = HomeGlowCyan,
            offset = 0.34f
        )
        glowSegment(
            start = androidx.compose.ui.geometry.Offset(left + cellWidth * 0.10f, bottom),
            end = androidx.compose.ui.geometry.Offset(left + cellWidth * 1.28f, bottom),
            color = HomeGlowCyan,
            offset = 0.48f
        )
        glowSegment(
            start = androidx.compose.ui.geometry.Offset(left + cellWidth, top + cellHeight * 1.08f),
            end = androidx.compose.ui.geometry.Offset(left + cellWidth, bottom - cellHeight * 0.05f),
            color = HomeGlowBlue,
            offset = 0.62f
        )
        glowSegment(
            start = androidx.compose.ui.geometry.Offset(left + cellWidth * 2f, top + cellHeight * 0.02f),
            end = androidx.compose.ui.geometry.Offset(left + cellWidth * 2f, top + cellHeight * 1.10f),
            color = HomeGlowIndigo,
            offset = 0.78f
        )
    }
}

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val compact = maxWidth < 330.dp
            Text(
                text = "SUDOKU MENTOR",
                modifier = Modifier.fillMaxWidth(),
                fontSize = if (compact) 20.sp else 22.sp,
                fontFamily = HomeBodyFont,
                fontWeight = FontWeight.Light,
                color = SudokuPalette.WarmWhite,
                letterSpacing = if (compact) 2.4.sp else 3.4.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = "Pensado para resolver,\nno para adivinar.",
            fontSize = 17.sp,
            fontFamily = HomeBodyFont,
            fontWeight = FontWeight.Light,
            color = SudokuPalette.WarmWhite,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeBadge() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.58f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, SudokuPalette.MentorCyan)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(SudokuPalette.MentorIndigo, RoundedCornerShape(999.dp))
            )
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SudokuPalette.MentorPurple, Color.Transparent)
                        )
                    )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(SudokuPalette.MentorCyan, RoundedCornerShape(999.dp))
            )
            Text(
                text = stringResource(R.string.home_badge),
                color = SudokuPalette.BadgeText,
                fontFamily = HomeBodyFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(SudokuPalette.MentorPurple, RoundedCornerShape(999.dp))
            )
        }
    }
}

@Composable
private fun HomeActionButton(text: String, onClick: () -> Unit, isPrimary: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    val backgroundModifier = if (isPrimary) {
        Modifier.background(SudokuPalette.PrimaryButtonGradient, shape)
    } else {
        Modifier.background(SudokuPalette.PrimaryButtonSecondaryContainer, shape)
    }
    val borderColor = if (isPrimary) {
        Color.White.copy(alpha = 0.22f)
    } else {
        SudokuPalette.WarmWhite.copy(alpha = 0.74f)
    }

    Box(
        modifier = modifier
            .height(if (isPrimary) 44.dp else 40.dp)
            .defaultMinSize(minWidth = 124.dp)
            .then(backgroundModifier)
            .border(BorderStroke(1.dp, borderColor), shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontFamily = HomeBodyFont,
            fontWeight = FontWeight.Light,
            maxLines = 1
        )
        if (isPrimary) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp)
                    .size(28.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF010413
)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(hasSavedGame = true, onNewGameClick = {}, onContinueClick = {})
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF010413
)
@Composable
fun HomeScreenNoSavedGamePreview() {
    HomeScreenContent(hasSavedGame = false, onNewGameClick = {}, onContinueClick = {})
}
