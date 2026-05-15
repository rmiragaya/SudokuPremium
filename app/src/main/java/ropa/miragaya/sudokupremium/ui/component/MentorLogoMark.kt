package ropa.miragaya.sudokupremium.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun MentorLogoMark(
    modifier: Modifier = Modifier,
    outerRadius: Dp = 28.dp,
    cellRadius: Dp = 10.dp,
    showPanel: Boolean = true
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(outerRadius),
        color = SudokuPalette.HomePanel.copy(alpha = if (showPanel) 0.94f else 0f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
        shadowElevation = if (showPanel) 10.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .background(
                    color = SudokuPalette.BoardBackground,
                    shape = RoundedCornerShape(outerRadius * 0.72f)
                )
                .border(
                    width = 2.dp,
                    color = SudokuPalette.CellHintBorder.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(outerRadius * 0.72f)
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { row ->
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { col ->
                            val color = when {
                                row == 1 && col == 1 -> SudokuPalette.TextAccent
                                col == 2 && (row == 0 || row == 2) -> SudokuPalette.CellHintBorder
                                else -> SudokuPalette.TextAccent.copy(alpha = 0.18f)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(color = color, shape = RoundedCornerShape(cellRadius))
                                    .border(
                                        width = 1.dp,
                                        color = color.copy(alpha = 0.62f),
                                        shape = RoundedCornerShape(cellRadius)
                                    )
                            )
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val start = Offset(size.width * 0.79f, size.height * 0.22f)
                val pivot = Offset(size.width * 0.52f, size.height * 0.50f)
                val end = Offset(size.width * 0.79f, size.height * 0.78f)
                val accent = Color(0xFFF9D778)

                val path = Path().apply {
                    moveTo(start.x, start.y)
                    lineTo(pivot.x, pivot.y)
                    lineTo(end.x, end.y)
                }

                drawPath(
                    path = path,
                    color = accent.copy(alpha = 0.88f),
                    style = Stroke(
                        width = 5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawLine(
                    color = accent.copy(alpha = 0.88f),
                    start = start,
                    end = end,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawPath(
                    path = path,
                    color = SudokuPalette.BoardBackground.copy(alpha = 0.56f),
                    style = Stroke(
                        width = 1.7.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawLine(
                    color = SudokuPalette.BoardBackground.copy(alpha = 0.56f),
                    start = start,
                    end = end,
                    strokeWidth = 1.7.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
