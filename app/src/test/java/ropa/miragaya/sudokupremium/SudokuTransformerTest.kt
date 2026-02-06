package ropa.miragaya.sudokupremium

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.BoardGenerator
import ropa.miragaya.sudokupremium.domain.generator.SudokuTransformer
import ropa.miragaya.sudokupremium.domain.model.Board

class SudokuTransformerTest {

    private val transformer = SudokuTransformer()

    @Test
    fun `transform maintains sudoku rules (Fuzz Testing)`() {

        val validBoard = BoardGenerator.generateFilledBoard()

        repeat(100) { iteration ->
            val transformedBoard = transformer.transform(validBoard)

            assertTrue("Iteración $iteration: El tablero debe estar lleno",
                transformedBoard.cells.all { it.value != null })

            assertTrue("Iteración $iteration: Filas rotas", areRowsValid(transformedBoard))
            assertTrue("Iteración $iteration: Columnas rotas", areColsValid(transformedBoard))
            assertTrue("Iteración $iteration: Cajas rotas", areBoxesValid(transformedBoard))
        }
    }

    @Test
    fun `transform actually changes the board`() {
        val validBoard = BoardGenerator.generateFilledBoard()
        val transformedBoard = transformer.transform(validBoard)

        assertNotEquals(validBoard, transformedBoard)
    }

    private fun areRowsValid(board: Board): Boolean {
        return (0 until 9).all { r ->
            val values = board.cells.filter { it.row == r }.mapNotNull { it.value }
            values.toSet().size == 9
        }
    }

    private fun areColsValid(board: Board): Boolean {
        return (0 until 9).all { c ->
            val values = board.cells.filter { it.col == c }.mapNotNull { it.value }
            values.toSet().size == 9
        }
    }

    private fun areBoxesValid(board: Board): Boolean {
        return (0 until 9).all { b ->
            val values = board.cells.filter { it.box == b }.mapNotNull { it.value }
            values.toSet().size == 9
        }
    }
}