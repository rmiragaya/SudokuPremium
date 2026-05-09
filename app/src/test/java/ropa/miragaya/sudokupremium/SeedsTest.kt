package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertEquals
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.Seeds
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class SeedsTest {

    @Test
    fun `all generator seeds are valid 81-cell boards`() {
        val seedsByDifficulty = mapOf(
            Difficulty.EASY to Seeds.EASY_SEEDS,
            Difficulty.MEDIUM to Seeds.MEDIUM_SEEDS,
            Difficulty.HARD to Seeds.HARD_SEEDS,
            Difficulty.EXPERT to Seeds.EXPERT_SEEDS
        )

        seedsByDifficulty.forEach { (difficulty, seeds) ->
            seeds.forEachIndexed { index, seed ->
                assertEquals(
                    "Seed $index de $difficulty debe tener 81 caracteres",
                    81,
                    seed.length
                )
                Board.fromGridString(seed)
            }
        }
    }
}
