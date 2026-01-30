package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

interface SolvingStrategy {
    val name: String
    val difficulty: Difficulty

    /**
     * Intenta aplicar la estrategia al tablero UNA vez.
     * Retorna el nuevo Board con el cambio aplicado,
     * o null si esta estrategia no encontró nada.
     */
    fun apply(board: Board): Board?
}