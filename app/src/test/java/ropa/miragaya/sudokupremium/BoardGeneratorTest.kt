package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.BoardGenerator
import ropa.miragaya.sudokupremium.domain.model.Board

class BoardGeneratorTest {

    @Test
    fun `generateFilledBoard returns a valid full board`() {
        val board = BoardGenerator.generateFilledBoard()

        assertTrue("El tablero debería estar lleno", board.cells.all { it.value != null })

        val validatedBoard = board.validateConflicts()
        assertFalse("El tablero generado no debería tener errores", validatedBoard.cells.any { it.isError })

        printBoard(board)
    }

    private fun printBoard(board: Board) {
        board.rows.forEach { row ->
            println(row.map { it.value ?: 0 }.joinToString(" "))
        }
    }
}