package ropa.miragaya.sudokupremium.domain.model

fun Board.initializeCandidates(): Board {
    val newCells = this.cells.map { cell ->
        if (cell.value != null) {
            cell.copy(notes = emptySet())
        } else {
            val usedInRow = this.cells
                .filter { it.row == cell.row && it.value != null }
                .map { it.value!! }

            val usedInCol = this.cells
                .filter { it.col == cell.col && it.value != null }
                .map { it.value!! }

            val usedInBox = this.cells
                .filter { it.box == cell.box && it.value != null }
                .map { it.value!! }

            val candidates = (1..9).toSet() - (usedInRow + usedInCol + usedInBox).toSet()

            cell.copy(notes = candidates)
        }
    }
    return Board(newCells)
}