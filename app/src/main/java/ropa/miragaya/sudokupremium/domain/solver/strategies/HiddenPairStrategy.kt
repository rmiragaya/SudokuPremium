package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class HiddenPairStrategy : SolvingStrategy {

    override val name = "Hidden Pair"
    override val difficulty = Difficulty.MEDIUM

    override fun apply(board: Board): Board? {
        val allGroups = board.rows + board.cols + board.boxes

        for (group in allGroups) {
            val cellIds = group.map { it.id }
            val result = findHiddenPairsInGroup(board, cellIds)
            if (result != null) return result
        }
        return null
    }

    private fun findHiddenPairsInGroup(board: Board, groupIndices: List<Int>): Board? {
        val candidatePositions = mutableMapOf<Int, MutableList<Int>>()

        for (index in groupIndices) {
            val cell = board.cells[index]
            if (cell.value == null) {
                for (note in cell.notes) {
                    candidatePositions.getOrPut(note) { mutableListOf() }.add(index)
                }
            }
        }

        val possiblePairs = candidatePositions.filter { it.value.size == 2 }

        val entries = possiblePairs.entries.toList()

        for (i in 0 until entries.size) {
            for (j in i + 1 until entries.size) {
                val (num1, positions1) = entries[i]
                val (num2, positions2) = entries[j]

                if (positions1 == positions2) {

                    val idx1 = positions1[0]
                    val idx2 = positions1[1]

                    val cell1 = board.cells[idx1]
                    val cell2 = board.cells[idx2]

                    val pair = setOf(num1, num2)

                    if (cell1.notes.any { it !in pair } || cell2.notes.any { it !in pair }) {

                        val newCells = board.cells.toMutableList()

                        newCells[idx1] = cell1.copy(notes = pair)
                        newCells[idx2] = cell2.copy(notes = pair)

                        return Board(newCells)
                    }
                }
            }
        }
        return null
    }
}