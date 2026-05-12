package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun ButtonStyleExploration() {
    SudokuPremiumTheme {
        Column(
            modifier = Modifier
                .background(SudokuPalette.MainGradient)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Sistema de botones",
                style = MaterialTheme.typography.headlineSmall,
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tres direcciones para unificar primario, secundario y acción sensible.",
                style = MaterialTheme.typography.bodyMedium,
                color = SudokuPalette.TextSecondary
            )

            ButtonStyleSet(
                title = "1. Azul profundo",
                description = "Más app premium: gradiente profundo, borde suave y altura contenida.",
                primary = ButtonVisual.Gradient(
                    brush = SudokuPalette.PrimaryButtonGradient,
                    border = SudokuPalette.PrimaryButtonBorder
                ),
                secondary = ButtonVisual.Filled(
                    container = SudokuPalette.PrimaryButtonSecondaryContainer,
                    border = SudokuPalette.PrimaryButtonSecondaryBorder,
                    content = SudokuPalette.PrimaryButtonSecondaryContent
                )
            )

            ButtonStyleSet(
                title = "2. Vidrio tranquilo",
                description = "Más moderno y liviano: superficie oscura con borde azul y primario luminoso.",
                primary = ButtonVisual.Filled(
                    container = Color(0xFF5DB7FF),
                    border = Color(0xFFB7E2FF).copy(alpha = 0.52f),
                    content = Color.White
                ),
                secondary = ButtonVisual.Filled(
                    container = Color(0xFF171C2C),
                    border = Color(0xFF4B79B6),
                    content = Color(0xFFBFE2FF)
                )
            )

            ButtonStyleSet(
                title = "3. Borde activo",
                description = "Más sobrio para juego: botones oscuros, primario por borde/brillo y menos bloque azul.",
                primary = ButtonVisual.OutlinedGlow(
                    container = Color(0xFF182033),
                    glow = Color(0xFF67BCFF),
                    content = Color.White
                ),
                secondary = ButtonVisual.Filled(
                    container = Color(0xFF202438),
                    border = Color(0xFF323C5E),
                    content = Color(0xFFCAD3E8)
                )
            )
        }
    }
}

@Composable
private fun ButtonStyleSet(title: String, description: String, primary: ButtonVisual, secondary: ButtonVisual) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
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

            MentorPreviewButton(
                text = "Nueva partida",
                visual = primary,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MentorPreviewButton(
                    text = "Continuar",
                    visual = secondary,
                    modifier = Modifier.weight(1f)
                )
                MentorPreviewButton(
                    text = "Ahora no",
                    visual = secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MentorPreviewButton(text: String, visual: ButtonVisual, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(26.dp)
    val baseModifier = modifier
        .height(50.dp)
        .clickable(onClick = {})

    when (visual) {
        is ButtonVisual.Filled -> {
            Box(
                modifier = baseModifier
                    .background(visual.container, shape)
                    .border(1.dp, visual.border, shape),
                contentAlignment = Alignment.Center
            ) {
                ButtonLabel(text = text, color = visual.content)
            }
        }

        is ButtonVisual.Gradient -> {
            Box(
                modifier = baseModifier
                    .background(visual.brush, shape)
                    .border(1.dp, visual.border, shape),
                contentAlignment = Alignment.Center
            ) {
                ButtonLabel(text = text, color = Color.White)
            }
        }

        is ButtonVisual.OutlinedGlow -> {
            Box(
                modifier = baseModifier
                    .drawWithCache {
                        val stroke = 1.6.dp.toPx()
                        val glowStroke = 5.dp.toPx()
                        onDrawBehind {
                            drawRoundRect(
                                color = visual.glow.copy(alpha = 0.18f),
                                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                                style = Stroke(width = glowStroke)
                            )
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        visual.glow.copy(alpha = 0.96f),
                                        Color(0xFF356AFF).copy(alpha = 0.54f),
                                        visual.glow.copy(alpha = 0.72f)
                                    ),
                                    start = Offset.Zero,
                                    end = Offset(size.width, size.height)
                                ),
                                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                                style = Stroke(width = stroke)
                            )
                        }
                    }
                    .background(visual.container, shape),
                contentAlignment = Alignment.Center
            ) {
                ButtonLabel(text = text, color = visual.content)
            }
        }
    }
}

@Composable
private fun ButtonLabel(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

private sealed interface ButtonVisual {
    data class Filled(val container: Color, val border: Color, val content: Color) : ButtonVisual

    data class Gradient(val brush: Brush, val border: Color) : ButtonVisual

    data class OutlinedGlow(val container: Color, val glow: Color, val content: Color) : ButtonVisual
}

@Preview(
    name = "Button Style Exploration",
    showBackground = true,
    backgroundColor = 0xFF12141C,
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun ButtonStyleExplorationPreview() {
    ButtonStyleExploration()
}
