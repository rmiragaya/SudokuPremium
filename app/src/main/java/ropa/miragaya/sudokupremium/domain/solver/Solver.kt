package ropa.miragaya.sudokupremium.domain.solver

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.solver.strategies.HiddenSingleStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.IntersectionRemovalStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.NakedSingleStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.SolvingStrategy
import ropa.miragaya.sudokupremium.domain.solver.utils.SudokuDebugUtils

class Solver {

    // lista de estrategias para resolver
    private val strategies: List<SolvingStrategy> = listOf(
        NakedSingleStrategy(),
        HiddenSingleStrategy(),
        IntersectionRemovalStrategy()
    )

    fun solve(initialBoard: Board): SolveResult {
        if (initialBoard.cells.any { it.isError }) return SolveResult.Invalid

        var currentBoard = initialBoard.initializeCandidates()

        var logicApplied = true
        var maxDifficulty = Difficulty.EASY

        while (logicApplied && !currentBoard.isSolved()) {
            logicApplied = false

            for (strategy in strategies) {
                val nextBoard = strategy.apply(currentBoard)

                if (nextBoard != null) {

                    val wasNumberPlaced = currentBoard.cells.count { it.value != null } !=
                            nextBoard.cells.count { it.value != null }

                    if (wasNumberPlaced) {
                        currentBoard = nextBoard.initializeCandidates()
                    } else {
                        currentBoard = nextBoard
                    }

                    logicApplied = true
                    if (strategy.difficulty.ordinal > maxDifficulty.ordinal) {
                        maxDifficulty = strategy.difficulty
                    }

                    break
                }
            }
        }

        return if (currentBoard.isSolved()) {
            SolveResult.Success(currentBoard, maxDifficulty)
        } else {
            SolveResult.Failure(currentBoard)
        }
    }
}