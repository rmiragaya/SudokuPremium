package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.generator.SudokuTransformer
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver

class SudokuGeneratorTest {

    private val solver = Solver()
    private val transformer = SudokuTransformer()
    private val generator = SudokuGenerator(solver, transformer)

    @Test
    fun `generated puzzle clues match the solution`() {
        val puzzle = generator.generate(Difficulty.MEDIUM)

        val visibleBoard = puzzle.board
        val solution = puzzle.solvedBoard

        assertNotNull("Debe tener solucion cargada", solution)

        visibleBoard.cells.forEach { cell ->
            if (cell.value != null) {
                assertEquals(
                    "La pista en (${cell.row},${cell.col}) no coincide con la solucion",
                    solution.cells[cell.id].value,
                    cell.value
                )
            }
        }
    }

    @Test
    fun `generated puzzles are solvable with requested difficulty`() {
        Difficulty.values().forEach { difficulty ->
            val puzzle = generator.generate(difficulty)
            val result = solver.solve(puzzle.board)

            assertEquals("La dificultad publica debe coincidir con la pedida", difficulty, puzzle.difficulty)
            assertTrue("El puzzle $difficulty debe tener solucion unica", result is SolveResult.Success)
            assertEquals(
                "El solver debe clasificar el puzzle como $difficulty",
                difficulty,
                (result as SolveResult.Success).difficulty
            )
        }
    }

    @Test
    fun `generator produces solvable puzzles consistently`() {
        repeat(20) {
            val puzzle = generator.generate(Difficulty.MEDIUM)
            val result = solver.solve(puzzle.board)

            assertTrue("El puzzle debe tener solucion unica", result is SolveResult.Success)
        }
    }
}
