package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class HiddenSingleStrategy : SolvingStrategy {
    override val name = "Hidden Single"
    override val difficulty = Difficulty.EASY

    override fun apply(board: Board): StrategyResult? {
        for (rowIndex in 0 until 9) {
            val result = findHiddenSingleInGroup(
                board = board,
                cellIds = board.rows[rowIndex].map { it.id },
                containerType = "fila",
                containerIndex = rowIndex
            )
            if (result != null) return result
        }

        for (colIndex in 0 until 9) {
            val result = findHiddenSingleInGroup(
                board = board,
                cellIds = board.cols[colIndex].map { it.id },
                containerType = "columna",
                containerIndex = colIndex
            )
            if (result != null) return result
        }

        for (boxIndex in 0 until 9) {
            val result = findHiddenSingleInGroup(
                board = board,
                cellIds = board.boxes[boxIndex].map { it.id },
                containerType = "caja",
                containerIndex = boxIndex
            )
            if (result != null) return result
        }
        return null
    }

    private fun findHiddenSingleInGroup(
        board: Board,
        cellIds: List<Int>,
        containerType: String,
        containerIndex: Int
    ): StrategyResult? {
        val candidateCounts = mutableMapOf<Int, MutableList<Int>>()
        (1..9).forEach { candidateCounts[it] = mutableListOf() }

        for (cellId in cellIds) {
            val cell = board.cells.find { it.id == cellId } ?: continue

            if (cell.value == null) {

                for (candidate in cell.notes) {
                    candidateCounts[candidate]?.add(cellId)
                }
            }
        }

        for ((number, positions) in candidateCounts) {
            if (positions.size == 1) {
                val targetCellId = positions.first()
                val context = StrategyContext.HiddenSingle(
                    candidateNumber = number,
                    containerType = containerType,
                    containerIndex = containerIndex,
                    cellId = targetCellId
                )
                return StrategyResult(board.withCellValue(targetCellId, number), context)
            }
        }
        return null
    }
}
