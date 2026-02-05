package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class NakedTripleStrategy : SolvingStrategy {

    override val name = "Naked Triple"
    override val difficulty = Difficulty.HARD

    override fun apply(board: Board): Board? {
        val allGroups = board.rows + board.cols + board.boxes

        for (group in allGroups) {
            val cellIds = group.map { it.id }
            val result = findNakedTriplesInGroup(board, cellIds)
            if (result != null) return result
        }
        return null
    }

    private fun findNakedTriplesInGroup(board: Board, groupIndices: List<Int>): Board? {
        val potentialCells = groupIndices.filter { index ->
            val cell = board.cells[index]
            cell.value == null && cell.notes.size in 2..3
        }

        if (potentialCells.size < 3) return null

        for (i in 0 until potentialCells.size) {
            for (j in i + 1 until potentialCells.size) {
                for (k in j + 1 until potentialCells.size) {

                    val idx1 = potentialCells[i]
                    val idx2 = potentialCells[j]
                    val idx3 = potentialCells[k]

                    val cell1 = board.cells[idx1]
                    val cell2 = board.cells[idx2]
                    val cell3 = board.cells[idx3]

                    val unionNotes = (cell1.notes + cell2.notes + cell3.notes).toSet()

                    if (unionNotes.size == 3) {

                        val tripleIndices = setOf(idx1, idx2, idx3)
                        var changesMade = false
                        val newCells = board.cells.toMutableList()

                        for (index in groupIndices) {
                            if (index !in tripleIndices) {
                                val targetCell = newCells[index]
                                if (targetCell.value == null) {
                                    val notesToRemove = targetCell.notes.intersect(unionNotes)

                                    if (notesToRemove.isNotEmpty()) {
                                        val newNotes = targetCell.notes - notesToRemove
                                        newCells[index] = targetCell.copy(notes = newNotes)
                                        changesMade = true
                                    }
                                }
                            }
                        }

                        if (changesMade) {
                            return Board(newCells)
                        }
                    }
                }
            }
        }
        return null
    }
}