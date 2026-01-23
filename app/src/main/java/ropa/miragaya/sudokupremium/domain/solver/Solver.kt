package ropa.miragaya.sudokupremium.domain.solver

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class Solver {

    // Lista de estrategias para resolver
    private val strategies: List<SolvingStrategy> = listOf(
        NakedSingleStrategy(),
        HiddenSingleStrategy()
    )

    fun solve(initialBoard: Board): SolveResult {
        if (initialBoard.cells.any { it.isError }) {
            return SolveResult.Invalid
        }

        var currentBoard = initialBoard
        var logicApplied = true

        // Empezamos asumiendo que es Facilísimo
        var maxDifficulty = Difficulty.EASY

        while (logicApplied && !currentBoard.isSolved()) {
            logicApplied = false

            for (strategy in strategies) {
                val nextBoard = strategy.apply(currentBoard)

                if (nextBoard != null) {
                    currentBoard = nextBoard
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