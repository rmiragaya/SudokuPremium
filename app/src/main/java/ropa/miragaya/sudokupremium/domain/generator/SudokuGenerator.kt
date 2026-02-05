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

        Log.d("SUDOKU_TRACE", "--- Iniciamos generación ---")

        // generacion por seeds
        val seedPuzzle = generateFromSeeds(targetDifficulty)
        if (seedPuzzle != null) return seedPuzzle

        // generacion por fuerza bruta
        return generateProcedural(targetDifficulty)
    }

    private fun generateFromSeeds(targetDifficulty: Difficulty): SudokuPuzzle? {
        val seeds = when (targetDifficulty) {
            Difficulty.EXPERT -> Seeds.EXPERT_SEEDS
            Difficulty.HARD -> Seeds.HARD_SEEDS
            else -> emptyList()
        }

        if (seeds.isEmpty()) return null

        val startTime = System.currentTimeMillis()

        val randomSeedString = seeds.random()
        val baseBoard = Board.fromGridString(randomSeedString)
        val newBoard = transformer.transform(baseBoard)

        val solvedResult = solver.solve(newBoard)
        val solvedBoard = (solvedResult as? SolveResult.Success)?.board ?: newBoard

        val puzzle = SudokuPuzzle(newBoard, solvedBoard, targetDifficulty)

        logMetrics(
            success = true,
            target = targetDifficulty,
            actual = targetDifficulty,
            attempts = 1,
            startTime = startTime,
            board = newBoard
        )

        // Debug visual
        // todo solo enviar en debug
        SudokuDebugUtils.logPuzzleGenerated(puzzle)

        return puzzle
    }

    private fun generateProcedural(targetDifficulty: Difficulty): SudokuPuzzle {
        val startTime = System.currentTimeMillis()
        var bestPuzzle: SudokuPuzzle? = null
        val maxAttempts = 50

        for (attempt in 1..maxAttempts) {
            val puzzle = generateRandomPuzzle(targetDifficulty)

            if (puzzle.difficulty == targetDifficulty) {
                logMetrics(
                    success = true,
                    target = targetDifficulty,
                    actual = puzzle.difficulty,
                    attempts = attempt,
                    startTime = startTime,
                    board = puzzle.board
                )

                Log.d("SUDOKU_TRACE", "--- REPRODUCIENDO SOLUCIÓN ---")
                solver.solve(puzzle.board, logSteps = true)
                SudokuDebugUtils.logPuzzleGenerated(puzzle)

                return puzzle
            }

            if (bestPuzzle == null || puzzle.difficulty.ordinal > bestPuzzle.difficulty.ordinal) {
                bestPuzzle = puzzle
            }
        }

        val finalPuzzle = bestPuzzle!!

        logMetrics(
            success = false,
            target = targetDifficulty,
            actual = finalPuzzle.difficulty,
            attempts = maxAttempts,
            startTime = startTime,
            board = finalPuzzle.board
        )
        Log.w("SUDOKU_ANALYTICS", "⚠️ Devolviendo aproximación.")

        return finalPuzzle
    }

    private fun logMetrics(
        success: Boolean,
        target: Difficulty,
        actual: Difficulty,
        attempts: Int,
        startTime: Long,
        board: Board
    ) {
        val duration = System.currentTimeMillis() - startTime
        val metrics = GenerationMetrics(
            success = success,
            targetDifficulty = target,
            actualDifficulty = actual,
            attempts = attempts,
            durationMs = duration,
            boardString = board.toGridString()
        )

        val tag = "SUDOKU_ANALYTICS"
        if (success) {
            Log.i(tag, metrics.toString())
        } else {
            Log.w(tag, metrics.toString())
        }
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