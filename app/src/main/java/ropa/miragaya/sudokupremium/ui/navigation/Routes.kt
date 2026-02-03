package ropa.miragaya.sudokupremium.ui.navigation

import kotlinx.serialization.Serializable
import ropa.miragaya.sudokupremium.domain.model.Difficulty


// Home
@Serializable
object HomeRoute

// Sudoku
@Serializable
data class GameRoute(
    val createNew: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY
)