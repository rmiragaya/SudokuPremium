package ropa.miragaya.sudokupremium

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class StrategyContextMessageTest {

    @Test
    fun `hidden single message uses highlighted target cell`() {
        val context = StrategyContext.HiddenSingle(
            candidateNumber = 7,
            containerType = "fila",
            containerIndex = 2,
            cellId = 22
        )

        val message = context.getSuccessMessage(7)

        assertTrue(message.contains("fila 3"))
        assertTrue(message.contains("número 7"))
        assertTrue(message.contains("casilla resaltada"))
        assertEquals(listOf(22), context.highlightCellIds)
    }

    @Test
    fun `naked pair message uses highlighted pair cells and candidates`() {
        val context = StrategyContext.NakedPair(
            pairedCandidates = listOf(7, 3),
            containerType = "fila",
            containerIndex = 0,
            pairCellIds = listOf(0, 1)
        )

        val message = context.getEliminationMessage(mapOf(2 to listOf(3), 4 to listOf(7)))

        assertTrue(message.contains("fila 1"))
        assertTrue(message.contains("3 y 7"))
        assertTrue(message.contains("dos casillas resaltadas"))
        assertTrue(message.contains("casillas marcadas en rojo"))
        assertEquals(listOf(0, 1), context.highlightCellIds)
    }

    @Test
    fun `naked pair message avoids coordinates inside a column`() {
        val context = StrategyContext.NakedPair(
            pairedCandidates = listOf(7, 3),
            containerType = "columna",
            containerIndex = 3,
            pairCellIds = listOf(66, 75)
        )

        val message = context.getEliminationMessage(mapOf(3 to listOf(3), 21 to listOf(7)))

        assertTrue(message.contains("columna 4"))
        assertTrue(message.contains("casillas resaltadas"))
        assertTrue(message.contains("3 y 7"))
    }

    @Test
    fun `x wing message explains rectangle axes`() {
        val context = StrategyContext.XWing(
            candidateNumber = 4,
            baseLineType = "fila",
            baseLineIndices = listOf(1, 6),
            coverLineType = "columna",
            coverLineIndices = listOf(2, 8)
        )

        val message = context.getEliminationMessage(mapOf(20 to listOf(4)))

        assertTrue(message.contains("número 4"))
        assertTrue(message.contains("casillas resaltadas"))
        assertTrue(message.contains("casillas marcadas en rojo"))
        assertTrue(message.contains("X-Wing"))
        assertEquals(listOf(11, 17, 56, 62), context.highlightCellIds)
    }
}
