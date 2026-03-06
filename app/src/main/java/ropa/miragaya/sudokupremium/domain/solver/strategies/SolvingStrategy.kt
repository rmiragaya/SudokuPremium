package ropa.miragaya.sudokupremium.domain.solver.strategies

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.StrategyContext

interface SolvingStrategy {
    val name: String
    val difficulty: Difficulty

    fun apply(board: Board): StrategyResult?

    fun findAll(board: Board): List<StrategyResult> {
        val result = apply(board)
        return if (result != null) listOf(result) else emptyList()
    }
}

data class StrategyResult(
    val newBoard: Board,
    val context: StrategyContext
)