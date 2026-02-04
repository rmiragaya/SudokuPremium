package ropa.miragaya.sudokupremium.domain.solver

import android.util.Log
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.solver.strategies.HiddenSingleStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.IntersectionRemovalStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.NakedPairStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.NakedSingleStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.SolvingStrategy
import ropa.miragaya.sudokupremium.domain.solver.utils.SudokuDebugUtils
import javax.inject.Inject

class Solver @Inject constructor() {

    // lista de estrategias para resolver
    private val strategies: List<SolvingStrategy> = listOf(
        NakedSingleStrategy(),
        HiddenSingleStrategy(),
        IntersectionRemovalStrategy(),
        NakedPairStrategy()// porque esta iria antes que IntersectionRemovalStrategy?
    )

    fun solve(initialBoard: Board, logSteps: Boolean = false): SolveResult {
        if (initialBoard.cells.any { it.isError }) return SolveResult.Invalid

        var currentBoard = initialBoard.initializeCandidates()

        var logicApplied = true
        var maxDifficulty = Difficulty.EASY

        while (logicApplied && !currentBoard.isSolved()) {
            logicApplied = false

            for (strategy in strategies) {
                val nextBoard = strategy.apply(currentBoard)

                if (nextBoard != null) {

                    if (logSteps) {
                        SudokuDebugUtils.logStep(strategy.name, currentBoard, nextBoard)
                    }

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

    fun findNextHint(board: Board): SudokuHint? {

        val currentBoard = board.initializeCandidates()

        for (strategy in strategies) {
            val nextBoard = strategy.apply(currentBoard)

            if (nextBoard != null) {

                for (i in 0 until 81) {
                    val oldCell = currentBoard.cells[i]
                    val newCell = nextBoard.cells[i]

                    if (oldCell.value != newCell.value) {
                        return SudokuHint(
                            strategyName = strategy.name,
                            description = "Esta celda solo tiene una opción posible.", // todo Personalizar esto según la estrategia
                            row = oldCell.row,
                            col = oldCell.col,
                            value = newCell.value
                        )
                    }

                    if (oldCell.notes != newCell.notes) {
                        val removed = oldCell.notes - newCell.notes
                        if (removed.isNotEmpty()) {
                            return SudokuHint(
                                strategyName = strategy.name,
                                description = "Los números $removed no pueden ir aquí por lógica de ${strategy.name}.",
                                row = oldCell.row,
                                col = oldCell.col,
                                value = null,
                                notesRemoved = removed.toList()
                            )
                        }
                    }
                }
            }
        }
        return null
    }
}