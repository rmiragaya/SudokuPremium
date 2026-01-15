package ropa.miragaya.sudokupremium.domain.model

data class Board(
    val cells: List<Cell>
) {
    // 1. Validación Board debe tener 81 celdas.
    init {
        require(cells.size == 81) { "Sudoku debe tener exactamente 81 celdas" }
    }

    val rows: List<List<Cell>> by lazy {
        cells.groupBy { it.row }.toSortedMap().values.toList()
    }

    val cols: List<List<Cell>> by lazy {
        cells.groupBy { it.col }.toSortedMap().values.toList()
    }

    val boxes: List<List<Cell>> by lazy {
        cells.groupBy { it.box }.toSortedMap().values.toList()
    }

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
}