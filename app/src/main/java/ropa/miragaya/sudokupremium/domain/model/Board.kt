package ropa.miragaya.sudokupremium.domain.model

data class Board(
    val cells: List<Cell>
) {
    // 1. Validación Board debe tener 81 celdas.
    init {
        require(cells.size == 81) { "Sudoku debe tener exactamente 81 celdas" }
    }

    val rows: List<List<Cell>>
        get() = cells.groupBy { it.row }.toSortedMap().values.toList()

    val cols: List<List<Cell>>
        get() = cells.groupBy { it.col }.toSortedMap().values.toList()

    val boxes: List<List<Cell>>
        get() = cells.groupBy { it.box }.toSortedMap().values.toList()

    // Por si acaso
    fun getCell(row: Int, col: Int): Cell = cells.first { it.row == row && it.col == col }

    fun withCellValue(cellId: Int, newValue: Int): Board {
        val newCells = cells.map { cell ->
            if (cell.id == cellId) {
                cell.copy(value = newValue)
            } else {
                cell
            }
        }
        return Board(newCells)
    }

    fun withCellCleared(cellId: Int): Board {
        val newCells = cells.map { cell ->
            if (cell.id == cellId) {
                cell.copy(value = null)
            } else {
                cell
            }
        }
        return Board(newCells)
    }

    fun validateConflicts(): Board {
        val errorIds = mutableSetOf<Int>()

        // Busco duplicados
        fun checkGroup(group: List<Cell>) {

            val valueCounts = group.filter { it.value != null }.groupBy { it.value }

            valueCounts.forEach { (_, cellsWithSameValue) ->
                if (cellsWithSameValue.size > 1) {
                    errorIds.addAll(cellsWithSameValue.map { it.id })
                }
            }
        }

        rows.forEach { checkGroup(it) }
        cols.forEach { checkGroup(it) }
        boxes.forEach { checkGroup(it) }

        val newCells = cells.map { cell ->
            cell.copy(isError = errorIds.contains(cell.id))
        }

        return Board(newCells)
    }

    fun getCellsWithValue(value: Int): Set<Int> {
        return cells.filter { it.value == value }.map { it.id }.toSet()
    }

    fun getPeers(cellId: Int): Set<Int> {
        val target = cells.find { it.id == cellId } ?: return emptySet()

        // Buscamos IDs de misma fila, columna y caja
        val rowPeers = rows[target.row].map { it.id }
        val colPeers = cols[target.col].map { it.id }
        val boxPeers = boxes[target.box].map { it.id }

        // Unimos toddo en un Set (evita duplicados) y return celda seleccionada
        return (rowPeers + colPeers + boxPeers).toSet() - target.id
    }
    companion object {
        fun createEmpty(): Board {
            val emptyCells = List(81) { index ->
                Cell(
                    id = index,
                    row = index / 9,
                    col = index % 9,
                    box = (index / 9 / 3) * 3 + (index % 9 / 3),
                    value = null,
                    isGiven = false
                )
            }
            return Board(emptyCells)
        }
    }

}