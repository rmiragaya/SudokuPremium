package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board

class BoardTest {

    @Test
    fun `clearing a cell removes notes too`() {
        val board = Board.createEmpty()
            .withNoteToggle(cellId = 0, note = 3)
            .withNoteToggle(cellId = 0, note = 7)

        val clearedBoard = board.withCellCleared(cellId = 0)

        assertTrue(clearedBoard.cells[0].notes.isEmpty())
    }

    @Test
    fun `complete board with duplicated values is not solved`() {
        val invalidBoard = Board.fromGridString(
            "112345678" +
                "456789123" +
                "789123456" +
                "234567891" +
                "567891234" +
                "891234567" +
                "345678912" +
                "678912345" +
                "912345678"
        )

        assertTrue(invalidBoard.hasConflicts())
        assertFalse(invalidBoard.isSolved())
    }

    @Test
    fun `fromGridString rejects invalid board length`() {
        assertThrows(IllegalArgumentException::class.java) {
            Board.fromGridString("0".repeat(80))
        }
    }
}
