package ropa.miragaya.sudokupremium.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ropa.miragaya.sudokupremium.domain.model.Difficulty

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeSeconds: Long,
    val difficulty: Difficulty,
    val timestamp: Long = System.currentTimeMillis()
)