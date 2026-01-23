package ropa.miragaya.sudokupremium.domain.generator

import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver
import javax.inject.Inject
import kotlin.random.Random

class SudokuGenerator @Inject constructor(
    private val solver: Solver
) {

    /**
     * Intenta generar un puzzle que coincida con la dificultad deseada.
     * Como el proceso es aleatorio, ponemos un límite de intentos.
     */
    fun generate(targetDifficulty: Difficulty): SudokuPuzzle {

        var bestPuzzle: SudokuPuzzle? = null

        repeat(10) {
            val puzzle = generateRandomPuzzle(targetDifficulty)

            // Si le pegamos justo a la dificultad, cortamos y entregamos.
            if (puzzle.difficulty == targetDifficulty) {
                return puzzle
            }
            bestPuzzle = puzzle
        }

        return bestPuzzle!!
    }

    private fun generateRandomPuzzle(maxDifficultyAllowed: Difficulty): SudokuPuzzle {
        val solvedBoard = BoardGenerator.generateFilledBoard()
        var currentBoard = solvedBoard

        val cellIndices = (0..80).toMutableList().shuffled(Random)
        var currentDifficulty = Difficulty.EASY

        for (index in cellIndices) {

            if (currentBoard.cells[index].value == null) continue

            val nextBoard = currentBoard.withCellValue(index, null)

            val result = solver.solve(nextBoard)

            when (result) {
                is SolveResult.Success -> {
                    if (result.difficulty.ordinal <= maxDifficultyAllowed.ordinal) {
                        currentBoard = nextBoard
                        currentDifficulty = result.difficulty
                    }
                }
                is SolveResult.Failure, SolveResult.Invalid -> {
                }
            }
        }

        return SudokuPuzzle(
            board = currentBoard,
            solvedBoard = solvedBoard,
            difficulty = currentDifficulty
        )
    }
}