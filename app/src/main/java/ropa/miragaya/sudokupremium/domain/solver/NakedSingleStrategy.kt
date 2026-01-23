package ropa.miragaya.sudokupremium.domain.solver

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class NakedSingleStrategy : SolvingStrategy {
    override val name = "Naked Single"
    override val difficulty = Difficulty.EASY

    /** Devuelve un Board nuevo, con el cambio aplicado, o null si no encontró nada. */
    override fun apply(board: Board): Board? {
        val emptyCells = board.cells.filter { it.value == null }

        for (cell in emptyCells) {
            val candidates = board.calculateCandidates(cell.id)

            if (candidates.size == 1) {
                val valueToPlace = candidates.first()

                return board.withCellValue(cell.id, valueToPlace)
            }
        }
        return null
    }
}