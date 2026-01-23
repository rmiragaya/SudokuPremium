package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.NakedSingleStrategy

class NakedSingleTest {

    @Test
    fun `should find number when only one candidate exists`() {

        var board = Board.createEmpty()

        // Llenamos la fila 0 con 1..8
        (0..7).forEach { colIndex ->
            board = board.withCellValue(colIndex, colIndex + 1)
        }

        // 2. EXECUTE
        val strategy = NakedSingleStrategy()
        val newBoard = strategy.apply(board)

        // 3. ASSERT
        assertNotNull("La estrategia debería haber encontrado un movimiento", newBoard)

        val targetCell = newBoard!!.cells.find { it.id == 8 }
        assertEquals("El valor puesto debería ser 9", 9, targetCell?.value)
    }
}