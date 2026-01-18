package ropa.miragaya.sudokupremium.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

@Entity(tableName = "saved_game")
data class GameEntity(
    @PrimaryKey val id: Int = 1,
    val board: Board,
    val elapsedTimeSeconds: Long = 0,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val lastPlayed: Long = System.currentTimeMillis()
)