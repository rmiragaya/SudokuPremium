package ropa.miragaya.sudokupremium.domain.generator

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.utils.SudokuDebugUtils
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

    private fun generateRandomPuzzle(maxDifficultyAllowed: Difficulty): SudokuPuzzle {
        val solvedBoard = BoardGenerator.generateFilledBoard()
        var currentBoard = solvedBoard
        val cellIndices = (0..80).toMutableList().shuffled(Random)

        // asumimos es easy
        var currentDifficulty = Difficulty.EASY

        for (index in cellIndices) {
            if (currentBoard.cells[index].value == null) continue

            // borramos un nuero
            val nextBoard = currentBoard.withCellValue(index, null)

            // solver intenta resolverlo
            val result = solver.solve(nextBoard)

            when (result) {
                is SolveResult.Success -> {
                    // resuelto y dentro de la misma dificultad anterior
                    if (result.difficulty.ordinal <= maxDifficultyAllowed.ordinal) {
                        if (result.difficulty.ordinal > currentDifficulty.ordinal) {
                            Log.d("SUDOKU_SOLVER", "🚀 EL PUZZLE SUBIÓ DE NIVEL: ${currentDifficulty.name} -> ${result.difficulty.name}")
                        }
                        currentBoard = nextBoard
                        currentDifficulty = result.difficulty
                    }
                    // resuelto pero mas dificil que la dificultad anterior
                }
                is SolveResult.Failure, SolveResult.Invalid -> {
                    Log.d("SUDOKU_SOLVER", "Solve Result: ${result.javaClass.simpleName}")
                }
            }
        }

        // guardamos los valores generados como isGiven
        val finalCells = currentBoard.cells.map { cell ->
            if (cell.value != null) cell.copy(isGiven = true) else cell
        }

        return SudokuPuzzle(
            board = Board(finalCells),
            solvedBoard = solvedBoard,
            difficulty = currentDifficulty
        )
    }
}