package ropa.miragaya.sudokupremium.domain.model

data class Cell(
    val id: Int, // Indice 0-80
    val row: Int,
    val col: Int,
    val box: Int,
    val value: Int?,
    val isGiven: Boolean,
    val isError: Boolean = false,
    val notes: Set<Int> = emptySet()
)