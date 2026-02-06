package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.BoardGenerator
import ropa.miragaya.sudokupremium.domain.model.Board

class BoardGeneratorTest {

    @Test
    fun `generateFilledBoard returns a valid full sudoku board (Independent Check)`() {
        val board = BoardGenerator.generateFilledBoard()

        assertTrue("El tablero debe estar lleno", board.cells.all { it.value != null })

        assertTrue("Todas las filas deben ser válidas", areRowsValid(board))
        assertTrue("Todas las columnas deben ser válidas", areColsValid(board))
        assertTrue("Todas las cajas 3x3 deben ser válidas", areBoxesValid(board))
    }

    @Test
    fun `generateFilledBoard generates different boards (Randomness Check)`() {
        val board1 = BoardGenerator.generateFilledBoard()
        val board2 = BoardGenerator.generateFilledBoard()

        assertNotEquals("Dos llamadas consecutivas deberían dar tableros distintos", board1, board2)
    }

    @Test
    fun `generateFilledBoard is stable under stress`() {

        val startTime = System.currentTimeMillis()

        repeat(20) {
            val board = BoardGenerator.generateFilledBoard()
            assertTrue(board.cells.all { it.value != null })
        }

        val endTime = System.currentTimeMillis()
        println("Generar 20 tableros tomó: ${endTime - startTime}ms")
    }


    private fun areRowsValid(board: Board): Boolean {

        return (0 until 9).all { rowIndex ->
            val rowValues = board.cells.filter { it.row == rowIndex }.mapNotNull { it.value }
            rowValues.toSet().size == 9
        }
    }

    private fun areColsValid(board: Board): Boolean {
        return (0 until 9).all { colIndex ->
            val colValues = board.cells.filter { it.col == colIndex }.mapNotNull { it.value }
            colValues.toSet().size == 9
        }
    }

    private fun areBoxesValid(board: Board): Boolean {
        return (0 until 9).all { boxIndex ->
            val boxValues = board.cells.filter { cell ->
                val cellBox = (cell.row / 3) * 3 + (cell.col / 3)
                cellBox == boxIndex
            }.mapNotNull { it.value }

            boxValues.toSet().size == 9
        }
    }
}