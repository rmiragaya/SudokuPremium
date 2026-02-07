package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.solver.Solver
import ropa.miragaya.sudokupremium.domain.solver.strategies.HiddenSingleStrategy
import ropa.miragaya.sudokupremium.domain.solver.strategies.NakedSingleStrategy
import javax.inject.Inject

class HintGenerator @Inject constructor(
    private val solver: Solver,
    private val messageFactory: HintMessageFactory
) {

    private val nakedChecker = NakedSingleStrategy()
    private val hiddenChecker = HiddenSingleStrategy()

    fun findAllHints(board: Board): List<SudokuHint> {
        val currentBoard = board.initializeCandidates()
        val strategies = solver.strategies

        val cleaningHints = mutableListOf<SudokuHint>()

        for (strategy in strategies) {
            val boardsFound = strategy.findAll(currentBoard)

            if (boardsFound.isNotEmpty()) {
                val strategyHints = boardsFound.mapNotNull { newBoard ->
                    createHintFromDiff(strategy.name, currentBoard, newBoard)
                }

                for (hint in strategyHints) {
                    val score = calculateImpactScore(hint, currentBoard)

                    if (score > 0) {
                        return listOf(hint)
                    } else {
                        cleaningHints.add(hint)
                    }
                }
            }
        }

        return cleaningHints
    }

    private fun calculateImpactScore(hint: SudokuHint, originalBoard: Board): Int {
        if (hint.value != null) return 3

        val boardAfterHint = applyHintToBoard(originalBoard, hint)

        if (nakedChecker.apply(boardAfterHint) != null) return 2

        if (hiddenChecker.apply(boardAfterHint) != null) return 1

        return 0
    }

    private fun applyHintToBoard(board: Board, hint: SudokuHint): Board {
        if (hint.notesRemoved.isEmpty()) return board

        val targetIndex = (hint.row * 9) + hint.col
        val cell = board.cells[targetIndex]
        val newNotes = cell.notes - hint.notesRemoved.toSet()

        val newCells = board.cells.toMutableList()
        newCells[targetIndex] = cell.copy(notes = newNotes)

        return Board(newCells)
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