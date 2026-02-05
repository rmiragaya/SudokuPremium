package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class YWingStrategy : SolvingStrategy {

    override val name = "Y-Wing"
    override val difficulty = Difficulty.EXPERT

    override fun apply(board: Board): Board? {

        val biValueCells = board.cells.filter { it.value == null && it.notes.size == 2 }

        if (biValueCells.size < 3) return null

        for (pivot in biValueCells) {
            val pivotNotes = pivot.notes.toList() // Ej: [A, B]
            val noteA = pivotNotes[0]
            val noteB = pivotNotes[1]

            val possibleWingsA = findWings(board, pivot, noteA, biValueCells)
            val possibleWingsB = findWings(board, pivot, noteB, biValueCells)

            for (wing1 in possibleWingsA) {
                for (wing2 in possibleWingsB) {
                    if (wing1.id == wing2.id) continue

                    val noteC_fromWing1 = wing1.notes.first { it != noteA }
                    val noteC_fromWing2 = wing2.notes.first { it != noteB }

                    if (noteC_fromWing1 == noteC_fromWing2) {
                        val noteC = noteC_fromWing1

                        val victimCells = getCommonPeers(board, wing1, wing2)

                        var changesMade = false
                        val newCells = board.cells.toMutableList()

                        for (victim in victimCells) {
                            if (victim.value == null && victim.notes.contains(noteC)) {
                                val newNotes = victim.notes - noteC
                                newCells[victim.id] = victim.copy(notes = newNotes)
                                changesMade = true
                            }
                        }

                        if (changesMade) {
                            return Board(newCells)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun findWings(board: Board, pivot: Cell, matchNote: Int, allBiValueCells: List<Cell>): List<Cell> {
        val pivotPeers = board.getPeers(pivot.id)
        return allBiValueCells.filter { cell ->
            cell.id != pivot.id &&
                    pivotPeers.contains(cell.id) &&
                    cell.notes.contains(matchNote)
        }
    }

    private fun getCommonPeers(board: Board, cellA: Cell, cellB: Cell): List<Cell> {
        val peersA = board.getPeers(cellA.id)
        val peersB = board.getPeers(cellB.id)
        val commonIds = peersA.intersect(peersB)

        return commonIds.map { id -> board.cells.first { it.id == id } }
    }
}