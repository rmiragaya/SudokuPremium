package ropa.miragaya.sudokupremium.domain.generator

import android.util.Log
import javax.inject.Inject
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.model.analytics.GenerationMetrics
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.utils.SudokuDebugUtils

private const val GENERATION_ANALYTICS_TAG = "SUDOKU_ANALYTICS"

class SudokuGenerator @Inject constructor(private val solver: Solver, private val transformer: SudokuTransformer) {

    fun generate(targetDifficulty: Difficulty): SudokuPuzzle {
        val startTime = System.currentTimeMillis()
        val seeds = seedsFor(targetDifficulty)

        if (seeds.isEmpty()) throw IllegalStateException("Faltan semillas para $targetDifficulty")

        var lastActualDifficulty: Difficulty? = null

        seeds.shuffled().forEach { seed ->
            val baseBoard = Board.fromGridString(seed)
            val newBoard = transformer.transform(baseBoard)

            when (val solvedResult = solver.solve(newBoard)) {
                is SolveResult.Success -> {
                    lastActualDifficulty = solvedResult.difficulty
                    if (solvedResult.difficulty == targetDifficulty) {
                        val puzzle = SudokuPuzzle(newBoard, solvedResult.board, targetDifficulty)
                        logMetrics(startTime, targetDifficulty, solvedResult.difficulty, puzzle)
                        return puzzle
                    }
                }
                is SolveResult.Failure,
                SolveResult.Invalid -> Unit
            }
        }

        throw IllegalStateException(
            "No se pudo generar un sudoku $targetDifficulty. " +
                "Ultima dificultad resuelta: ${lastActualDifficulty ?: "sin solucion"}"
        )
    }

    private fun seedsFor(targetDifficulty: Difficulty): List<String> {
        return when (targetDifficulty) {
            Difficulty.EASY -> Seeds.EASY_SEEDS
            Difficulty.MEDIUM -> Seeds.MEDIUM_SEEDS
            Difficulty.HARD -> Seeds.HARD_SEEDS
            Difficulty.EXPERT -> Seeds.EXPERT_SEEDS
        }
    }

    private fun logMetrics(
        startTime: Long,
        targetDifficulty: Difficulty,
        actualDifficulty: Difficulty,
        puzzle: SudokuPuzzle
    ) {
        if (!BuildConfig.DEBUG) return

        val duration = System.currentTimeMillis() - startTime

        val metrics = GenerationMetrics(
            success = targetDifficulty == actualDifficulty,
            targetDifficulty = targetDifficulty,
            actualDifficulty = actualDifficulty,
            durationMs = duration,
            boardString = puzzle.board.toGridString()
        )

        Log.i(GENERATION_ANALYTICS_TAG, metrics.toString())

        SudokuDebugUtils.logPuzzleGenerated(puzzle)
    }
}
