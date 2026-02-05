package ropa.miragaya.sudokupremium.domain.model

data class Board(
    val cells: List<Cell>
) {
    init {
        require(cells.size == 81) { "Sudoku debe tener exactamente 81 celdas" }
    }

    val rows: List<List<Cell>>
        get() = cells.groupBy { it.row }.toSortedMap().values.toList()

    val cols: List<List<Cell>>
        get() = cells.groupBy { it.col }.toSortedMap().values.toList()

    val boxes: List<List<Cell>>
        get() = cells.groupBy { it.box }.toSortedMap().values.toList()

    fun playMove(cellId: Int, number: Int, isNoteMode: Boolean): Board {

        val cell = cells.find { it.id == cellId } ?: return this

        if (cell.isGiven) return this

        return if (isNoteMode) {
            if (cell.value == null) {
                withNoteToggle(cellId, number)
            } else {
                this
            }
        } else {
            withCellValue(cellId, number).validateConflicts()
        }
    }
    fun withCellValue(cellId: Int, newValue: Int?): Board {
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

        // busco duplicados
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

        // buscamos IDs de misma fila, columna y caja
        val rowPeers = rows[target.row].map { it.id }
        val colPeers = cols[target.col].map { it.id }
        val boxPeers = boxes[target.box].map { it.id }

        // unimos toddo en un Set (evita duplicados) y return celda seleccionada
        return (rowPeers + colPeers + boxPeers).toSet() - target.id
    }

    fun withNoteToggle(cellId: Int, note: Int): Board {
        val newCells = cells.map { cell ->
            if (cell.id == cellId) {
                val newNotes = if (cell.notes.contains(note)) {
                    cell.notes - note
                } else {
                    cell.notes + note
                }
                cell.copy(notes = newNotes)
            } else {
                cell
            }
        }
        return Board(newCells)
    }

    fun isSolved(): Boolean {
        return cells.none { it.value == null } && cells.none { it.isError }
    }

    fun toGridString(): String {
        val sb = StringBuilder()
        sb.append("\n┌───────┬───────┬───────┐\n")

        for (row in 0 until 9) {
            sb.append("│ ")
            for (col in 0 until 9) {
                val cell = cells[row * 9 + col]
                val value = cell.value?.toString() ?: "."
                sb.append("$value ")
                if ((col + 1) % 3 == 0 && col < 8) sb.append("│ ")
            }
            sb.append("│\n")
            if ((row + 1) % 3 == 0 && row < 8) {
                sb.append("├───────┼───────┼───────┤\n")
            }
        }
        sb.append("└───────┴───────┴───────┘")
        return sb.toString()
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
                    isGiven = false,
                    isError = false,
                    notes = emptySet()
                )
            }
            return Board(emptyCells)
        }

        /**
         * Crea un tablero desde un string de 81 caracteres (0 o . para vacíos)
         */
        fun fromGridString(str: String): Board {
            val cleanStr = str.replace(Regex("[^0-9.]"), "")

            val cells = cleanStr.mapIndexed { index, char ->
                val digit = if (char == '.') 0 else char.digitToInt()
                val value = if (digit == 0) null else digit

                val row = index / 9
                val col = index % 9
                val box = (row / 3) * 3 + (col / 3)

                Cell(
                    id = index,
                    row = row,
                    col = col,
                    box = box,
                    value = value,
                    isGiven = value != null // Si viene en la semilla, es una pista fija
                )
            }
            return Board(cells)
        }

    }

}