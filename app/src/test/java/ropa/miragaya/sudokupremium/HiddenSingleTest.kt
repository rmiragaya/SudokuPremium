package ropa.miragaya.sudokupremium

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.strategies.HiddenSingleStrategy

class HiddenSingleTest {

    @Test
    fun `should find hidden single when a candidate appears only once in a row`() {
        val emptyBoard = Board.createEmpty()
        val cells = emptyBoard.cells.toMutableList()

        cells[0] = cells[0].copy(notes = setOf(1, 2))
        cells[1] = cells[1].copy(notes = setOf(2, 3))
        cells[2] = cells[2].copy(notes = setOf(3, 4))

        val boardWithNotes = Board(cells)
        val strategy = HiddenSingleStrategy()

        val result = strategy.apply(boardWithNotes)

        assertNotNull("La estrategia deberia haber encontrado el Hidden Single", result)

        val targetCell = result!!.newBoard.cells[0]
        assertEquals("Deberia haber puesto el 1 porque era unico en la fila", 1, targetCell.value)
    }

    @Test
    fun `should find hidden single inside a Box 3x3`() {
        val emptyBoard = Board.createEmpty()
        val cells = emptyBoard.cells.toMutableList()

        val targetId = 10
        val box0Ids = emptyBoard.boxes[0].map { it.id }

        box0Ids.forEach { id ->
            if (id == targetId) {
                cells[id] = cells[id].copy(notes = setOf(5, 8, 9))
            } else {
                cells[id] = cells[id].copy(notes = setOf(8, 9))
            }
        }

        val boardWithNotes = Board(cells)
        val strategy = HiddenSingleStrategy()

        val result = strategy.apply(boardWithNotes)

        assertNotNull(result)
        assertEquals(5, result!!.newBoard.cells[targetId].value)
    }
}
