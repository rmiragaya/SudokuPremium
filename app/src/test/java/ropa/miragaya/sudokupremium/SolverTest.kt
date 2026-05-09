package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver

class SolverTest {

    private val solver = Solver()

    @Test
    fun `solver rejects boards with duplicated givens`() {
        val invalidBoard = Board.fromGridString(
            "110000000" +
                "000000000" +
                "000000000" +
                "000000000" +
                "000000000" +
                "000000000" +
                "000000000" +
                "000000000" +
                "000000000"
        )

        assertTrue(solver.solve(invalidBoard) is SolveResult.Invalid)
    }
}
