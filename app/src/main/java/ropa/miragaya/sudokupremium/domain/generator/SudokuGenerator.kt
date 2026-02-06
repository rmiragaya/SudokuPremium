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
    private val solver: Solver,
    private val transformer: SudokuTransformer
) {

    fun generate(targetDifficulty: Difficulty): SudokuPuzzle {
        val startTime = System.currentTimeMillis()

        val seeds = when (targetDifficulty) {
            Difficulty.EASY -> Seeds.EASY_SEEDS
            Difficulty.MEDIUM -> Seeds.MEDIUM_SEEDS
            Difficulty.HARD -> Seeds.HARD_SEEDS
            Difficulty.EXPERT -> Seeds.EXPERT_SEEDS
        }

        if (seeds.isEmpty()) throw IllegalStateException("Faltan semillas para $targetDifficulty")

        val randomSeedString = seeds.random()
        val baseBoard = Board.fromGridString(randomSeedString)
        val newBoard = transformer.transform(baseBoard)

        val solvedResult = solver.solve(newBoard, logSteps = true) // todo build debug false
        val solvedBoard = (solvedResult as? SolveResult.Success)?.board ?: newBoard

        val puzzle = SudokuPuzzle(newBoard, solvedBoard, targetDifficulty)

        logMetrics(startTime, puzzle)

        return puzzle
    }

    private fun logMetrics(startTime: Long, puzzle: SudokuPuzzle) {
        val duration = System.currentTimeMillis() - startTime

        val metrics = GenerationMetrics(
            success = true,
            targetDifficulty = puzzle.difficulty,
            actualDifficulty = puzzle.difficulty,
            durationMs = duration,
            boardString = puzzle.board.toGridString()
        )

        Log.i("SUDOKU_ANALYTICS", metrics.toString())

        SudokuDebugUtils.logPuzzleGenerated(puzzle)
    }
}