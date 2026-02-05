package ropa.miragaya.sudokupremium.domain.solver.utils

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle

object SudokuDebugUtils {

    private const val TAG = "SUDOKU_TRACE"

    fun logStep(strategyName: String, oldBoard: Board, newBoard: Board) {

        for (i in 0 until 81) {
            val oldCell = oldBoard.cells[i]
            val newCell = newBoard.cells[i]

            val coord = "(${oldCell.row + 1}, ${oldCell.col + 1})"

            if (oldCell.value != newCell.value) {
                Log.d(TAG, "✅ [${strategyName.padEnd(18)}] PUSO ${newCell.value} en $coord")
            }

            if (oldCell.notes != newCell.notes) {
                val removed = oldCell.notes - newCell.notes
                if (removed.isNotEmpty()) {
                    Log.d(TAG, "🧹 [${strategyName.padEnd(18)}] BORRÓ $removed en $coord")
                }
            }
        }
    }

    fun logPuzzleGenerated(puzzle: SudokuPuzzle) {
        Log.d(TAG, "╔════════════════════════════════════════════╗")
        Log.d(TAG, "║ 🎉 NUEVO PUZZLE GENERADO                   ║")
        Log.d(TAG, "╠════════════════════════════════════════════╣")
        Log.d(TAG, "║ 🏆 DIFICULTAD: ${puzzle.difficulty.name.padEnd(27)} ║")
        Log.d(TAG, "╚════════════════════════════════════════════╝")

        Log.d(TAG, "🙈 SOLUCIÓN (Spoilers):")
        Log.d(TAG, puzzle.solvedBoard.toGridString())

        Log.d(TAG, "==============================================")

        logSeed(puzzle)

    }

    private fun logSeed(puzzle: SudokuPuzzle) {
        Log.d(TAG, "🧬 SEMILLA (Copiar esto para Seeds.kt):")
        val rawString = puzzle.board.cells.joinToString("") { it.value?.toString() ?: "0" }
        Log.d(TAG, "\"$rawString\"")
    }
}