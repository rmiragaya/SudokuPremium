package ropa.miragaya.sudokupremium

import junit.framework.TestCase.assertTrue
import org.junit.Test
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.generator.SudokuTransformer
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.solver.SolveResult.Success
import ropa.miragaya.sudokupremium.domain.solver.Solver

class SudokuGeneratorTest {

    @Test
    fun `generate produces a solvable puzzle`() {
        val solver = Solver()
        val sudokuTransformer = SudokuTransformer()
        val generator = SudokuGenerator(solver, sudokuTransformer)

        val puzzle = generator.generate(Difficulty.EASY)

        assertTrue("El puzzle debe tener celdas vacías", puzzle.board.cells.any { it.value == null })

        val solveResult = solver.solve(puzzle.board)
        assertTrue("El puzzle generado debe ser resoluble", solveResult is Success)

        val clues = puzzle.board.cells.count { it.value != null }
        println("Puzzle generado!")
        println("Dificultad: ${puzzle.difficulty}")
        println("Pistas iniciales: $clues")
    }
}