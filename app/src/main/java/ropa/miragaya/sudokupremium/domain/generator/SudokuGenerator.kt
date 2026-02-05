package ropa.miragaya.sudokupremium.domain.generator

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.model.analytics.GenerationMetrics
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
        val startTime = System.currentTimeMillis()
        var bestPuzzle: SudokuPuzzle? = null

        val maxAttempts = 50

        for (attempt in 1..maxAttempts) {
            val puzzle = generateRandomPuzzle(targetDifficulty)

            // Encontramos la dificultad exacta
            if (puzzle.difficulty == targetDifficulty) {
                val endTime = System.currentTimeMillis()

                // 📝 LOGUEAMOS EL ÉXITO
                val metrics = GenerationMetrics(
                    success = true,
                    targetDifficulty = targetDifficulty,
                    actualDifficulty = puzzle.difficulty,
                    attempts = attempt,
                    durationMs = endTime - startTime,
                    boardString = puzzle.board.toGridString()
                )
                Log.i("SUDOKU_ANALYTICS", metrics.toString())

                Log.d("SUDOKU_TRACE", "--- REPRODUCIENDO SOLUCIÓN ---")
                solver.solve(puzzle.board, logSteps = true)

                SudokuDebugUtils.logPuzzleGenerated(puzzle)
                return puzzle
            }

            if (bestPuzzle == null || puzzle.difficulty.ordinal > bestPuzzle.difficulty.ordinal) {
                bestPuzzle = puzzle
            }
        }

        // 📝 LOGUEAMOS EL "FALLO" (Aproximación)
        val endTime = System.currentTimeMillis()
        val metrics = GenerationMetrics(
            success = false,
            targetDifficulty = targetDifficulty,
            actualDifficulty = bestPuzzle!!.difficulty,
            attempts = maxAttempts,
            durationMs = endTime - startTime,
            boardString = bestPuzzle.board.toGridString()
        )
        Log.w("SUDOKU_ANALYTICS", metrics.toString())

        return bestPuzzle
    }

    private fun generateRandomPuzzle(maxDifficultyAllowed: Difficulty): SudokuPuzzle {
        val solvedBoard = BoardGenerator.generateFilledBoard()
        var currentBoard = solvedBoard
        val cellIndices = (0..80).toMutableList().shuffled(Random)

        // asumimos es easy
        var currentDifficulty = Difficulty.EASY

        for (index in cellIndices) {
            if (currentBoard.cells[index].value == null) continue

            val nextBoard = currentBoard.withCellValue(index, null)

            val result = solver.solve(nextBoard)

            when (result) {
                is SolveResult.Success -> {
                    // resuelto y dentro de la misma dificultad anterior
                    if (result.difficulty.ordinal <= maxDifficultyAllowed.ordinal) {
                        if (result.difficulty.ordinal > currentDifficulty.ordinal) {
                            Log.d(
                                "SUDOKU_SOLVER",
                                "🚀 EL PUZZLE SUBIÓ DE NIVEL: ${currentDifficulty.name} -> ${result.difficulty.name}"
                            )
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