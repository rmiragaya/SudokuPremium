package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.strategies.HiddenSingleStrategy

class HiddenSingleTest {

    @Test
    fun `should find hidden single when a number can only go in one cell of a group`() {
        // en la primera fila dejo dos lugares vacios, el primero y el ultimo,
        // y lleno el resto de casilleros de la fila con num del 2 al 8.
        // entonces solo pueden ir los numeros 1 y 9 en las casillas vacias.
        // agrego un 1 en la fila 2 columna 8 para que no pueda ser puesto en la fila 1 columna 8.
        // quiere decir que el 1 es el hidden single en la casilla 1

        var board = Board.createEmpty()

        // lleno fila 0 (excepto puntas)
        (1..7).forEach { col -> board = board.withCellValue(col, col + 1) } // Val: 2,3,4,5,6,7,8

        // el 1 en la última columna poniendo un 1 en ID 17
        board = board.withCellValue(17, 1)

        val strategy = HiddenSingleStrategy()
        val resultBoard = strategy.apply(board)

        assertNotNull("Hidden Single encontrado", resultBoard)
        val cell0 = resultBoard!!.cells.find { it.id == 0 }
        assertEquals(1, cell0?.value)
    }
}