package ropa.miragaya.sudokupremium.ui.game

import ropa.miragaya.sudokupremium.domain.model.Board

data class GameState(
    val board: Board,
    val selectedCellId: Int? = null,
    val isComplete: Boolean = false
)