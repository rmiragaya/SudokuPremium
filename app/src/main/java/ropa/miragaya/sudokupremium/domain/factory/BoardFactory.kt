package ropa.miragaya.sudokupremium.domain.factory

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell

object BoardFactory {
    fun fromString(source: String): Board {
        require(source.length == 81) { "El mapa debe tener 81 caracteres" }

        val cells = source.mapIndexed { index, char ->
            val digit = char.digitToInt()
            val row = index / 9
            val col = index % 9
            val box = (row / 3) * 3 + (col / 3)

            Cell(
                id = index,
                row = row,
                col = col,
                box = box,
                value = if (digit == 0) null else digit,
                isGiven = digit != 0
            )
        }
        return Board(cells)
    }
}