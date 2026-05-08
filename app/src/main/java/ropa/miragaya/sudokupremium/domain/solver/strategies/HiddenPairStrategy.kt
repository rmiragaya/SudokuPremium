package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class HiddenPairStrategy : SolvingStrategy {

    override val name = "Hidden Pair"
    override val difficulty = Difficulty.MEDIUM

    override fun apply(board: Board): StrategyResult? {
        return findAll(board).firstOrNull()
    }

    override fun findAll(board: Board): List<StrategyResult> {
        val foundResults = mutableListOf<StrategyResult>()

        for (rowIndex in 0 until 9) {
            val result = findHiddenPairsInGroup(
                board = board,
                groupIndices = board.rows[rowIndex].map { it.id },
                containerType = "fila",
                containerIndex = rowIndex
            )
            if (result != null) foundResults.add(result)
        }

        for (colIndex in 0 until 9) {
            val result = findHiddenPairsInGroup(
                board = board,
                groupIndices = board.cols[colIndex].map { it.id },
                containerType = "columna",
                containerIndex = colIndex
            )
            if (result != null) foundResults.add(result)
        }

        for (boxIndex in 0 until 9) {
            val result = findHiddenPairsInGroup(
                board = board,
                groupIndices = board.boxes[boxIndex].map { it.id },
                containerType = "caja",
                containerIndex = boxIndex
            )
            if (result != null) foundResults.add(result)
        }

        return foundResults
    }

    private fun findHiddenPairsInGroup(
        board: Board,
        groupIndices: List<Int>,
        containerType: String,
        containerIndex: Int
    ): StrategyResult? {
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

                        val context = StrategyContext.HiddenPair(
                            pairedCandidates = pair.toList().sorted(),
                            containerType = containerType,
                            containerIndex = containerIndex,
                            pairCellIds = listOf(idx1, idx2)
                        )

                        return StrategyResult(Board(newCells), context)
                    }
                }
            }
        }
        return null
    }
}
