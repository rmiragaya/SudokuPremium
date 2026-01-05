package ropa.miragaya.sudokupremium.domain.model

data class Cell(
    val id: Int,             // Indice 0-80
    val row: Int,
    val col: Int,
    val box: Int,
    val value: Int?,         // El número real (null si está vacío)
    val isGiven: Boolean,    // TRUE si venía en el string original
    // val candidates: MutableSet<Int> = mutableSetOf() // Todavia no implementar
)