package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.util.concurrent.TimeUnit
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter

@Composable
fun VictoryKonfettiOverlay(modifier: Modifier = Modifier) {
    val parties = remember {
        listOf(
            victoryParty(position = Position.Relative(0.10, 0.02), angle = 35),
            victoryParty(position = Position.Relative(0.90, 0.02), angle = 145),
            victoryParty(position = Position.Relative(0.50, 0.00), angle = 90)
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
        spread = 62,
        speed = 8f,
        maxSpeed = 24f,
        damping = 0.88f,
        timeToLive = 2300L,
        colors = listOf(
            0xFF64B5F6.toInt(),
            0xFFFFD54F.toInt(),
            0xFF6ED6A5.toInt(),
            0xFFFFFFFF.toInt()
        ),
        position = position,
        emitter = Emitter(duration = 950, TimeUnit.MILLISECONDS).max(95)
    )
}
