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

    fun findAllHints(board: Board): List<SudokuHint> {
        var currentBoard = board.initializeCandidates()
        val strategies = solver.strategies

        val hintChain = mutableListOf<SudokuHint>()

        var safetyCounter = 20

        while (safetyCounter > 0) {
            safetyCounter--
            var progressMade = false

            for (strategy in strategies) {
                val boardsFound = strategy.findAll(currentBoard)

                if (boardsFound.isNotEmpty()) {
                    val newBoard = boardsFound.first()
                    val hint = createFullDiffHint(strategy.name, currentBoard, newBoard)

                    if (hint != null) {
                        hintChain.add(hint)

                        // tenemos pista que agrega un numero, devolvemos la lista de pistas al usuario
                        if (hint.valueToSet != null) return hintChain

                        currentBoard = newBoard
                        progressMade = true
                        break
                    }
                }
            }

            if (!progressMade) break
        }

        return hintChain
    }

    private fun createFullDiffHint(strategyName: String, oldBoard: Board, newBoard: Board): SudokuHint? {
        var targetCellIndex: Int? = null
        var valueToSet: Int? = null
        val notesToRemoveMap = mutableMapOf<Int, List<Int>>()

        for (i in 0 until 81) {
            val oldCell = oldBoard.cells[i]
            val newCell = newBoard.cells[i]

            if (oldCell.value != newCell.value && newCell.value != null) {
                targetCellIndex = i
                valueToSet = newCell.value
                break
            }

            if (oldCell.notes != newCell.notes) {
                val removed = (oldCell.notes - newCell.notes).toList()
                if (removed.isNotEmpty()) {
                    notesToRemoveMap[i] = removed
                }
            }
        }

        if (targetCellIndex != null && valueToSet != null) {
            return SudokuHint(
                strategyName = strategyName,
                description = messageFactory.getSuccessMessage(strategyName, valueToSet),
                targetCellIndex = targetCellIndex,
                valueToSet = valueToSet,
                stepBoard = oldBoard
            )
        } else if (notesToRemoveMap.isNotEmpty()) {
            return SudokuHint(
                strategyName = strategyName,
                description = messageFactory.getEliminationMessage(strategyName, notesToRemoveMap),
                notesToRemove = notesToRemoveMap,
                stepBoard = oldBoard
            )
        }
        return null
    }
}