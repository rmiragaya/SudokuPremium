package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class XWingStrategy : SolvingStrategy {

    override val name = "X-Wing"
    override val difficulty = Difficulty.HARD

    override fun apply(board: Board): Board? {
        // Probamos X-Wing en Filas (para eliminar en Columnas)
        val rowResult = findXWing(board, isRowBased = true)
        if (rowResult != null) return rowResult

        // Probamos X-Wing en Columnas (para eliminar en Filas)
        val colResult = findXWing(board, isRowBased = false)
        if (colResult != null) return colResult

        return null
    }

    private fun findXWing(board: Board, isRowBased: Boolean): Board? {
        // Iteramos por cada número posible (1 al 9)
        for (candidate in 1..9) {

            // 1. Buscamos en qué líneas (filas o cols) el candidato aparece EXACTAMENTE 2 VECES
            val potentialLines = mutableListOf<Pair<Int, List<Int>>>() // Pair(IndiceLinea, ListaIndicesPosicion)

            for (i in 0 until 9) {
                val lineCells = if (isRowBased) board.rows[i] else board.cols[i]

                // Filtramos celdas vacías que tengan el candidato en sus notas
                val cellsWithCandidate = lineCells.filter { it.value == null && it.notes.contains(candidate) }

                if (cellsWithCandidate.size == 2) {
                    // Guardamos el índice de la línea y las posiciones RELATIVAS (col o row index)
                    val positions = cellsWithCandidate.map { if (isRowBased) it.col else it.row }
                    potentialLines.add(i to positions)
                }
            }

            // 2. Necesitamos al menos 2 líneas para formar el rectángulo
            if (potentialLines.size < 2) continue

            // 3. Buscamos pares de líneas que tengan el candidato en las MISMAS posiciones
            for (i in 0 until potentialLines.size) {
                for (j in i + 1 until potentialLines.size) {
                    val (lineIdx1, pos1) = potentialLines[i]
                    val (lineIdx2, pos2) = potentialLines[j]

                    // ¡EUREKA! Forman un rectángulo perfecto
                    if (pos1 == pos2) {
                        val colOrRow1 = pos1[0]
                        val colOrRow2 = pos1[1]

                        // Ahora intentamos eliminar el candidato de las OTRAS líneas
                        // Si buscamos por filas, eliminamos en las columnas (y viceversa)
                        var changesMade = false
                        val newCells = board.cells.toMutableList()

                        // Las líneas transversales donde vamos a limpiar
                        val transversalIndices = listOf(colOrRow1, colOrRow2)

                        for (transversalIdx in transversalIndices) {
                            // Obtenemos la columna (si era rowBased) o la fila
                            val transversalCells = if (isRowBased) board.cols[transversalIdx] else board.rows[transversalIdx]

                            for (cell in transversalCells) {
                                // NO tocamos las celdas que forman el X-Wing (las esquinas del rectángulo)
                                val cellLineIdx = if (isRowBased) cell.row else cell.col
                                if (cellLineIdx != lineIdx1 && cellLineIdx != lineIdx2) {

                                    if (cell.value == null && cell.notes.contains(candidate)) {
                                        val newNotes = cell.notes - candidate
                                        // Actualizamos en la lista plana
                                        newCells[cell.id] = cell.copy(notes = newNotes)
                                        changesMade = true
                                    }
                                }
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
}