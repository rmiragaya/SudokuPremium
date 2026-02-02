package ropa.miragaya.sudokupremium.domain.solver.utils

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle

object SudokuDebugUtils {

    private const val TAG = "SUDOKU_SOLVER"

    fun logStep(strategyName: String, oldBoard: Board, newBoard: Board) {
        val changes = mutableListOf<String>()
        var valueChanged = false

        // comparamos celda por celda
        for (i in 0 until 81) {
            val oldCell = oldBoard.cells[i]
            val newCell = newBoard.cells[i]

            if (oldCell.value != newCell.value) {
                changes.add("✍️ PUSO un [${newCell.value}] en (${oldCell.row}, ${oldCell.col}) - Box ${oldCell.box}")
                valueChanged = true
            }

            if (oldCell.notes != newCell.notes) {
                val removed = oldCell.notes - newCell.notes
                if (removed.isNotEmpty()) {
                    changes.add("❌ BORRÓ nota(s) $removed en (${oldCell.row}, ${oldCell.col})")
                }
            }
        }

        Log.d(TAG, "⚡ ESTRATEGIA: $strategyName")
        changes.forEach { Log.d(TAG, "   $it") }

        // si se puso un número, mostramos el tablero completo para ver el progreso
        if (valueChanged) {
            printBoard(newBoard)
        }
        Log.d(TAG, "--------------------------------------------------")
    }

    fun logPuzzleGenerated(puzzle: SudokuPuzzle) {
        Log.d(TAG, "╔════════════════════════════════════════════╗")
        Log.d(TAG, "║ 🎉 NUEVO PUZZLE GENERADO                   ║")
        Log.d(TAG, "╠════════════════════════════════════════════╣")
        Log.d(TAG, "║ 🏆 DIFICULTAD: ${puzzle.difficulty.name.padEnd(27)} ║")
        Log.d(TAG, "╚════════════════════════════════════════════╝")

        Log.d(TAG, "🙈 SOLUCIÓN (Spoilers):")
        printBoard(puzzle.solvedBoard)

        Log.d(TAG, "==============================================")
    }

    private fun printBoard(board: Board) {
        val sb = StringBuilder()
        sb.append("\n┌───────┬───────┬───────┐\n")

        for (row in 0 until 9) {
            sb.append("│ ")
            for (col in 0 until 9) {
                val cell = board.cells.first { it.row == row && it.col == col }
                val value = cell.value?.toString() ?: "."
                sb.append("$value ")
                if ((col + 1) % 3 == 0 && col < 8) sb.append("│ ")
            }
            sb.append("│\n")
            if ((row + 1) % 3 == 0 && row < 8) {
                sb.append("├───────┼───────┼───────┤\n")
            }
        }
        sb.append("└───────┴───────┴───────┘")
        Log.d(TAG, sb.toString())
    }
}