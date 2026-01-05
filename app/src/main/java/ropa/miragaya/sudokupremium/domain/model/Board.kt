package ropa.miragaya.sudokupremium.domain.model

data class Board(
    val cells: List<Cell>
) {
    // 1. Validación defensiva: El tablero SIEMPRE debe tener 81 celdas.
    init {
        require(cells.size == 81) { "Sudoku debe tener exactamente 81 celdas" }
    }

    // 2. VISTAS PRE-CALCULADAS (Lazy)
    // Esto es performance pura y comodidad.
    // En lugar de hacer 'cells.filter { it.row == 3 }' cada vez (lento y feo),
    // accedés directo a 'board.rows[3]'.
    // Usamos 'by lazy' para que solo se calculen la primera vez que las pidas.
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
}