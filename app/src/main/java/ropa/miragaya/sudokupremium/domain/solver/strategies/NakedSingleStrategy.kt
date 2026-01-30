package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.solver.calculateCandidates

class NakedSingleStrategy : SolvingStrategy {
    override val name = "Naked Single"
    override val difficulty = Difficulty.EASY

    /** Devuelve un Board nuevo, con el cambio aplicado, o null si no encontró nada. */
    override fun apply(board: Board): Board? {
        val singleNoteCell = board.cells.find { it.value == null && it.notes.size == 1 }

        if (singleNoteCell != null) {
            val valueToPlace = singleNoteCell.notes.first()
            return board.withCellValue(singleNoteCell.id, valueToPlace)
        }
        return null
    }
}