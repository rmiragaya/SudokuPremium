package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class NakedPairStrategy : SolvingStrategy {

    override val name = "Naked Pair"
    override val difficulty = Difficulty.MEDIUM

    override fun apply(board: Board): StrategyResult? {
        return findAll(board).firstOrNull()
    }

    override fun findAll(board: Board): List<StrategyResult> {
        val foundResults = mutableListOf<StrategyResult>()

        // 1. Buscamos en Filas
        for (rowIndex in 0 until 9) {
            val rowCells = board.cells.filter { it.row == rowIndex }
            val result = findNakedPairsInGroup(board, rowCells, "fila", rowIndex)
            if (result != null) foundResults.add(result)
        }

        // 2. Buscamos en Columnas
        for (colIndex in 0 until 9) {
            val colCells = board.cells.filter { it.col == colIndex }
            val result = findNakedPairsInGroup(board, colCells, "columna", colIndex)
            if (result != null) foundResults.add(result)
        }

        // 3. Buscamos en Cajas
        for (boxIndex in 0 until 9) {
            val boxCells = board.cells.filter { it.box == boxIndex }
            val result = findNakedPairsInGroup(board, boxCells, "caja", boxIndex)
            if (result != null) foundResults.add(result)
        }

        return foundResults
    }

    private fun findNakedPairsInGroup(
        board: Board,
        groupCells: List<Cell>,
        containerType: String,
        containerIndex: Int
    ): StrategyResult? {

        val candidates = groupCells.filter { it.value == null && it.notes.size == 2 }

        val groupsByNotes = candidates.groupBy { it.notes }

        for ((notes, cells) in groupsByNotes) {
            if (cells.size == 2) {

                val pairIds = cells.map { it.id }.toSet()
                var changesMade = false
                val newCells = board.cells.toMutableList()

                for (cell in groupCells) {
                    if (cell.id !in pairIds) {
                        val targetCell = newCells[cell.id]

                        if (targetCell.value == null && targetCell.notes.any { it in notes }) {
                            val newNotes = targetCell.notes - notes
                            newCells[cell.id] = targetCell.copy(notes = newNotes)
                            changesMade = true
                        }
                    }
                }

                if (changesMade) {
                    val context = StrategyContext.NakedPair(
                        pairedCandidates = notes.toList(),
                        containerType = containerType,
                        containerIndex = containerIndex
                    )
                    return StrategyResult(Board(newCells), context)
                }
            }
        }
        return null
    }
}