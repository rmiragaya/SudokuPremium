package ropa.miragaya.sudokupremium

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.techniques.TechniqueTutorialFixtures

class TechniqueTutorialFixturesTest {

    private val strategies = Solver().strategies.associateBy { it.name }

    @Test
    fun `tutorial examples are detected by their strategy`() {
        TechniqueTutorialFixtures.all.forEach { example ->
            val strategy = strategies.getValue(example.strategyName)
            val board = example.toBoard()
            val result = strategy.apply(board)

            assertNotNull("${example.strategyName} did not detect ${example.title}", result)
            requireNotNull(result)

            assertEquals(example.strategyName, result.context.name)
            assertEquals(example.highlightCells, result.context.highlightCellIds)
            assertEquals(example.highlightBoxes, result.context.highlightBoxIndexes)

            if (example.removedNotes.isNotEmpty()) {
                assertEquals(example.removedNotes, removedNotes(board, result.newBoard))
            } else {
                assertTrue(
                    "${example.strategyName} should place a value in ${example.title}",
                    valueWasPlaced(board, result.newBoard)
                )
            }
        }
    }

    private fun removedNotes(oldBoard: Board, newBoard: Board): Map<Int, Set<Int>> {
        return oldBoard.cells.mapNotNull { oldCell ->
            val removed = oldCell.notes - newBoard.cells[oldCell.id].notes
            if (removed.isEmpty()) null else oldCell.id to removed
        }.toMap()
    }

    private fun valueWasPlaced(oldBoard: Board, newBoard: Board): Boolean {
        return oldBoard.cells.zip(newBoard.cells).any { (oldCell, newCell) ->
            oldCell.value == null && newCell.value != null
        }
    }
}
