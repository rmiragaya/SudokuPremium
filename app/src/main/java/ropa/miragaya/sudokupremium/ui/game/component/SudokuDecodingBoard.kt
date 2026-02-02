package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import kotlin.random.Random

@Composable
fun SudokuDecodingBoard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(15.dp))
            .background(SudokuPalette.BoardBackground)
            .border(2.dp, SudokuPalette.GridLine, RoundedCornerShape(15.dp))
    ) {
        repeat(9) { rowIndex ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(9) { colIndex ->

                    val rightBorder = if (colIndex == 2 || colIndex == 5) 2.dp else 0.5.dp
                    val bottomBorder = if (rowIndex == 2 || rowIndex == 5) 2.dp else 0.5.dp

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .drawWithContent {
                                drawContent()
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = rightBorder.toPx()
                                )
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = bottomBorder.toPx()
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        DecodingCell()
                    }
                }
            }
        }
    }
}

@Composable
private fun DecodingCell() {
    val alphaAnim = remember { Animatable(0f) }

    var number by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {

            delay(Random.nextLong(100, 2000))

            number = Random.nextInt(1, 10)

            alphaAnim.animateTo(
                targetValue = 0.6f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )

            delay(100)

            alphaAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }
    }

    Text(
        text = number.toString(),
        fontSize = 25.sp,
        fontWeight = FontWeight.Medium,
        color = SudokuPalette.TextAccent,
        modifier = Modifier.alpha(alphaAnim.value)
    )
}