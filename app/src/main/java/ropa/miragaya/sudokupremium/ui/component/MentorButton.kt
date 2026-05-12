package ropa.miragaya.sudokupremium.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

enum class MentorButtonVariant {
    Primary,
    Secondary
}

@Composable
fun MentorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MentorButtonVariant = MentorButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    showGlow: Boolean = false,
    height: Dp = 50.dp
) {
    val shape = RoundedCornerShape(26.dp)
    val contentColor = when (variant) {
        MentorButtonVariant.Primary -> SudokuPalette.TextOnAccent
        MentorButtonVariant.Secondary -> SudokuPalette.PrimaryButtonSecondaryContent
    }
    val borderColor = when (variant) {
        MentorButtonVariant.Primary -> SudokuPalette.PrimaryButtonBorder
        MentorButtonVariant.Secondary -> SudokuPalette.PrimaryButtonSecondaryBorder
    }
    val backgroundModifier = when (variant) {
        MentorButtonVariant.Primary -> Modifier.background(SudokuPalette.PrimaryButtonGradient, shape)
        MentorButtonVariant.Secondary -> Modifier.background(SudokuPalette.PrimaryButtonSecondaryContainer, shape)
    }

    Box(
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 124.dp)
            .then(glowModifier(showGlow = showGlow, shapeRadius = 26.dp))
            .then(backgroundModifier)
            .border(1.dp, borderColor, shape)
            .alpha(if (enabled) 1f else 0.48f)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = contentColor
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun glowModifier(showGlow: Boolean, shapeRadius: Dp): Modifier {
    if (!showGlow) return Modifier

    val transition = rememberInfiniteTransition(label = "mentor-button-glow")
    val glowProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mentor-button-glow-progress"
    )

    return Modifier.drawWithContent {
        drawContent()
        val glowWidth = size.width * 0.58f
        val startX = (size.width + glowWidth) * glowProgress - glowWidth
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    SudokuPalette.PrimaryButtonBorder.copy(alpha = 0.48f),
                    SudokuPalette.CellHintBorder.copy(alpha = 0.82f),
                    SudokuPalette.PrimaryButtonBorder.copy(alpha = 0.42f),
                    Color.Transparent
                ),
                start = Offset(startX, 0f),
                end = Offset(startX + glowWidth, size.height)
            ),
            cornerRadius = CornerRadius(shapeRadius.toPx(), shapeRadius.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
