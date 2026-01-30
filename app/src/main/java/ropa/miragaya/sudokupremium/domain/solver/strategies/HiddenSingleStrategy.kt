package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.solver.calculateCandidates
import kotlin.collections.iterator

class HiddenSingleStrategy : SolvingStrategy {
    override val name = "Hidden Single"
    override val difficulty = Difficulty.EASY

    override fun apply(board: Board): Board? {

        val allGroups = board.rows + board.cols + board.boxes

        for (group in allGroups) {
            val result = findHiddenSingleInGroup(board, group.map { it.id })
            if (result != null) return result
        }
        return null
    }

    private fun findHiddenSingleInGroup(board: Board, cellIds: List<Int>): Board? {
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
                return board.withCellValue(targetCellId, number)
            }
        }
        return null
    }
}