package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class HiddenSingleStrategy : SolvingStrategy {
    override val name = "Hidden Single"
    override val difficulty = Difficulty.EASY

    override fun apply(board: Board): StrategyResult? {

        // filas
        board.rows.forEachIndexed { index, rowCells ->
            findHiddenSingleInGroup(board, rowCells.map { it.id }, "fila", index)?.let { return it }
        }

        // columnas
        board.cols.forEachIndexed { index, colCells ->
            findHiddenSingleInGroup(board, colCells.map { it.id }, "columna", index)?.let { return it }
        }

        // cajas
        board.boxes.forEachIndexed { index, boxCells ->
            findHiddenSingleInGroup(board, boxCells.map { it.id }, "caja", index)?.let { return it }
        }

        return null
    }

    private fun findHiddenSingleInGroup(
        board: Board,
        cellIds: List<Int>,
        regionType: String,
        regionIndex: Int
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
                val targetCell = board.cells.first { it.id == targetCellId }

                return StrategyResult(
                    newBoard = board.withCellValue(targetCellId, number),
                    context = StrategyContext.HiddenSingle(
                        row = targetCell.row,
                        col = targetCell.col,
                        value = number,
                        regionType = regionType,
                        regionIndex = regionIndex
                    )
                )
            }
        }
        return null
    }
}