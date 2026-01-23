package ropa.miragaya.sudokupremium.domain.solver

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

sealed class SolveResult {
    data class Success(
        val board: Board,
        val difficulty: Difficulty // determina con la dificultad que se resolvio
    ) : SolveResult()

    data class Failure(val boardState: Board) : SolveResult()
    data object Invalid : SolveResult()
}