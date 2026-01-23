package ropa.miragaya.sudokupremium.domain.solver

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class HiddenSingleStrategy : SolvingStrategy {
    override val name = "Hidden Single"

    override val difficulty = Difficulty.EASY

    override fun apply(board: Board): Board? {

        // 1. Revisar Filas
        for (row in board.rows) {
            val result = findHiddenSingleInGroup(board, row.map { it.id })
            if (result != null) return result
        }

        // 2. Revisar Columnas
        for (col in board.cols) {
            val result = findHiddenSingleInGroup(board, col.map { it.id })
            if (result != null) return result
        }

        // 3. Revisar Cajas
        for (box in board.boxes) {
            val result = findHiddenSingleInGroup(board, box.map { it.id })
            if (result != null) return result
        }

        return null
    }

    private fun findHiddenSingleInGroup(board: Board, cellIds: List<Int>): Board? {
        val candidatePositions =  mutableMapOf<Int, MutableList<Int>>()

        (1..9).forEach { candidatePositions[it] = mutableListOf() }

        for (cellId in cellIds) {
            val cell = board.cells.find { it.id == cellId } ?: continue

            if (cell.value == null) {
                val candidates = board.calculateCandidates(cellId)
                for (candidate in candidates) {
                    candidatePositions[candidate]?.add(cellId)
                }
            }
        }

        // buscamos numero que solo tenga una posicion en fila, columna o box
        for ((number, positions) in candidatePositions) {
            if (positions.size == 1) {
                val targetCellId = positions.first()

                // ingresamos el numero
                return board.withCellValue(targetCellId, number)
            }
        }

        return null
    }
}