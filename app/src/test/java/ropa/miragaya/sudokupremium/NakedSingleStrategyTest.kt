package ropa.miragaya.sudokupremium

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.strategies.NakedSingleStrategy

class NakedSingleStrategyTest {

    private val strategy = NakedSingleStrategy()

    @Test
    fun `should apply move when a cell has exactly one note`() {
        val emptyBoard = Board.createEmpty()

        val targetCellId = 0
        val cellWithSingleNote = emptyBoard.cells[targetCellId].copy(notes = setOf(5))
        
        val cells = emptyBoard.cells.toMutableList()
        cells[targetCellId] = cellWithSingleNote
        val boardWithNotes = Board(cells)

        val resultBoard = strategy.apply(boardWithNotes)

        assertNotNull("La estrategia debería haber actuado", resultBoard)

        val modifiedCell = resultBoard!!.cells[targetCellId]
        assertEquals("La celda debería tener el valor 5", 5, modifiedCell.value)
        assertTrue("La celda ya no debería tener notas (o irrelevantes)", modifiedCell.value != null)
    }

    @Test
    fun `should return null when no cell has a single note`() {
        val emptyBoard = Board.createEmpty()

        val targetCellId = 0
        val cellWithMultipleNotes = emptyBoard.cells[targetCellId].copy(notes = setOf(1, 2))

        val cells = emptyBoard.cells.toMutableList()
        cells[targetCellId] = cellWithMultipleNotes
        val boardWithAmbiguity = Board(cells)

        val resultBoard = strategy.apply(boardWithAmbiguity)

        assertNull("La estrategia no debería hacer nada si hay 2 opciones", resultBoard)
    }
}