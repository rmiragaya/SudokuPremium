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

        assertNotNull("Debe tener solución cargada", solution)

        visibleBoard.cells.forEach { cell ->
            if (cell.value != null) {
                assertEquals(
                    "La pista en (${cell.row},${cell.col}) no coincide con la solución",
                    solution.cells[cell.id].value,
                    cell.value
                )
            }
        }
    }

    @Test
    fun `generated puzzle respects difficulty (sanity check)`() {

        val easyPuzzle = generator.generate(Difficulty.EASY)
        val expertPuzzle = generator.generate(Difficulty.EXPERT)

        val easyClues = easyPuzzle.board.cells.count { it.value != null }
        val expertClues = expertPuzzle.board.cells.count { it.value != null }

        println("Easy Clues: $easyClues | Expert Clues: $expertClues")

        assertTrue(
            "El modo experto debería tener menos (o iguales) pistas que el fácil",
            expertClues <= easyClues
        )
    }

    @Test
    fun `generator produces solvable puzzles consistently (Stress Test)`() {
        repeat(20) {
            val puzzle = generator.generate(Difficulty.MEDIUM)
            val result = solver.solve(puzzle.board)
            assertTrue("El puzzle debe tener solución única", result is SolveResult.Success)
        }
    }
}