package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import javax.inject.Inject

class IntersectionRemovalStrategy @Inject constructor() : SolvingStrategy {
    override val name = "Intersection Removal"
    override val difficulty = Difficulty.MEDIUM

    override fun apply(board: Board): Board? {
        return findAll(board).firstOrNull()
    }

    override fun findAll(board: Board): List<Board> {
        val foundBoards = mutableListOf<Board>()

        for (candidate in 1..9) {

            for (boxIndex in 0 until 9) {
                val boxCells = board.cells.filter { it.box == boxIndex }
                val candidateCells = boxCells.filter { it.notes.contains(candidate) }

                if (candidateCells.size < 2) continue

                val firstRow = candidateCells[0].row
                if (candidateCells.all { it.row == firstRow }) {
                    val victims = board.cells.filter {
                        it.row == firstRow && it.box != boxIndex && it.notes.contains(candidate)
                    }
                    if (victims.isNotEmpty()) {
                        foundBoards.add(removeNotes(board, victims.map { it.id }, candidate))
                    }
                }

                val firstCol = candidateCells[0].col
                if (candidateCells.all { it.col == firstCol }) {
                    val victims = board.cells.filter {
                        it.col == firstCol && it.box != boxIndex && it.notes.contains(candidate)
                    }
                    if (victims.isNotEmpty()) {
                        foundBoards.add(removeNotes(board, victims.map { it.id }, candidate))
                    }
                }
            }

            for (rowIndex in 0 until 9) {
                val rowCells = board.cells.filter { it.row == rowIndex }
                val candidateCells = rowCells.filter { it.notes.contains(candidate) }

                if (candidateCells.size < 2) continue

                val firstBox = candidateCells[0].box
                if (candidateCells.all { it.box == firstBox }) {
                    val victims = board.cells.filter {
                        it.box == firstBox && it.row != rowIndex && it.notes.contains(candidate)
                    }
                    if (victims.isNotEmpty()) {
                        foundBoards.add(removeNotes(board, victims.map { it.id }, candidate))
                    }
                }
            }

            for (colIndex in 0 until 9) {
                val colCells = board.cells.filter { it.col == colIndex }
                val candidateCells = colCells.filter { it.notes.contains(candidate) }

                if (candidateCells.size < 2) continue

                val firstBox = candidateCells[0].box
                if (candidateCells.all { it.box == firstBox }) {
                    val victims = board.cells.filter {
                        it.box == firstBox && it.col != colIndex && it.notes.contains(candidate)
                    }
                    if (victims.isNotEmpty()) {
                        foundBoards.add(removeNotes(board, victims.map { it.id }, candidate))
                    }
                }
            }
        }

        return foundBoards
    }

    private fun removeNotes(board: Board, cellIds: List<Int>, noteToRemove: Int): Board {
        val newCells = board.cells.map { cell ->
            if (cellIds.contains(cell.id)) {
                cell.copy(notes = cell.notes - noteToRemove)
            } else {
                cell
            }
        }
        return Board(newCells)
    }
}