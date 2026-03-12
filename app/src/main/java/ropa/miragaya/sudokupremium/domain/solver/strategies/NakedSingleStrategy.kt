package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

class NakedSingleStrategy : SolvingStrategy {
    override val name = "Naked Single"
    override val difficulty = Difficulty.EASY

    override fun apply(board: Board): StrategyResult? {
        val singleNoteCell = board.cells.find { it.value == null && it.notes.size == 1 }

        if (singleNoteCell != null) {
            val valueToPlace = singleNoteCell.notes.first()
            val newBoard =  board.withCellValue(singleNoteCell.id, valueToPlace)

            val context = StrategyContext.NakedSingle(
                row = singleNoteCell.row,
                col = singleNoteCell.col
            )

            return StrategyResult(newBoard, context)
        }
        return null
    }
}