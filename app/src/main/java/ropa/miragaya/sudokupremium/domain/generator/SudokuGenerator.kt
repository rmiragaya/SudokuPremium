package ropa.miragaya.sudokupremium.domain.generator

import ropa.miragaya.sudokupremium.domain.model.Board
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

            if (puzzle.difficulty == targetDifficulty) {
                return puzzle
            }
            bestPuzzle = puzzle
        }

        return bestPuzzle!!
    }

    private fun generateRandomPuzzle(targetDifficulty: Difficulty): SudokuPuzzle {
        val solvedBoard = BoardGenerator.generateFilledBoard()
        var currentBoard = solvedBoard

        val cellIndices = (0..80).toMutableList().shuffled(Random)
        var currentDifficulty = Difficulty.EASY

        val stopAtCluesCount = when (targetDifficulty) {
            Difficulty.EASY -> 40
            Difficulty.MEDIUM -> 32
            Difficulty.HARD -> 24
        }

        var cluesCount = 81

        for (index in cellIndices) {
            if (cluesCount <= stopAtCluesCount) break

            if (currentBoard.cells[index].value == null) continue

            val nextBoard = currentBoard.withCellValue(index, null)

            val result = solver.solve(nextBoard)

            when (result) {
                is SolveResult.Success -> {
                    if (result.difficulty.ordinal <= targetDifficulty.ordinal) {
                        currentBoard = nextBoard
                        currentDifficulty = result.difficulty
                        cluesCount--
                    }
                }
                is SolveResult.Failure, SolveResult.Invalid -> {
                }
            }
        }

        // las celdas que tenemos son las fijas para el sudoku (isGiven)
        val finalCells = currentBoard.cells.map { cell ->
            if (cell.value != null) {
                cell.copy(isGiven = true)
            } else {
                cell
            }
        }

        val finalBoard = Board(finalCells)

        return SudokuPuzzle(
            board = finalBoard,
            solvedBoard = solvedBoard,
            difficulty = currentDifficulty
        )
    }
}