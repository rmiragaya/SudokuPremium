package ropa.miragaya.sudokupremium.ui.navigation
import kotlinx.serialization.Serializable


// Home
@Serializable
object HomeRoute

// Sudoku
@Serializable
data class GameRoute(val createNew: Boolean = false)