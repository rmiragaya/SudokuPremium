package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.solver.Solver
import javax.inject.Inject

class HintGenerator @Inject constructor(
    private val solver: Solver,
    private val messageFactory: HintMessageFactory
) {

    fun findNextHint(board: Board): SudokuHint? {
        val currentBoard = board.initializeCandidates()
        val strategies = solver.strategies

        for (strategy in strategies) {
            val nextBoard = strategy.apply(currentBoard)

            if (nextBoard != null) {
                return createHintFromDiff(strategy.name, currentBoard, nextBoard)
            }
        }
        return null
    }

    private fun createHintFromDiff(strategyName: String, oldBoard: Board, newBoard: Board): SudokuHint? {
        for (i in 0 until 81) {
            val oldCell = oldBoard.cells[i]
            val newCell = newBoard.cells[i]

            if (oldCell.value != newCell.value && newCell.value != null) {
                return SudokuHint(
                    strategyName = strategyName,
                    description = messageFactory.getSuccessMessage(strategyName, newCell.value),
                    row = oldCell.row,
                    col = oldCell.col,
                    value = newCell.value,
                    notesRemoved = emptyList()
                )
            }

            if (oldCell.notes != newCell.notes) {
                val removed = (oldCell.notes - newCell.notes).toList()
                val kept = newCell.notes.toList()

                if (removed.isNotEmpty()) {
                    return SudokuHint(
                        strategyName = strategyName,
                        description = messageFactory.getEliminationMessage(strategyName, removed, kept),
                        row = oldCell.row,
                        col = oldCell.col,
                        value = null,
                        notesRemoved = removed
                    )
                }
            }
        }
        return null
    }
}