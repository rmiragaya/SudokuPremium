package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class NakedPairStrategy : SolvingStrategy {

    override val name = "Naked Pair"

    override val difficulty = Difficulty.MEDIUM

    override fun apply(board: Board): Board? {

        val allGroups = board.rows + board.cols + board.boxes

        for (group in allGroups) {
            val cellIds = group.map { it.id }
            val result = findNakedPairsInGroup(board, cellIds)
            if (result != null) return result
        }
        return null
    }

    private fun findNakedPairsInGroup(board: Board, groupIndices: List<Int>): Board? {

        val candidates = groupIndices.map { board.cells[it] }
            .filter { it.value == null && it.notes.size == 2 }

        val groupsByNotes = candidates.groupBy { it.notes }

        for ((notes, cells) in groupsByNotes) {
            if (cells.size == 2) {

                val pairIds = cells.map { it.id }.toSet()

                var changesMade = false
                val newCells = board.cells.toMutableList()

                for (index in groupIndices) {

                    if (index !in pairIds) {
                        val targetCell = newCells[index]

                        if (targetCell.value == null && targetCell.notes.any { it in notes }) {
                            val newNotes = targetCell.notes - notes
                            newCells[index] = targetCell.copy(notes = newNotes)
                            changesMade = true
                        }
                    }
                }

                if (changesMade) {
                    return Board(newCells)
                }
            }
        }
        return null
    }
}