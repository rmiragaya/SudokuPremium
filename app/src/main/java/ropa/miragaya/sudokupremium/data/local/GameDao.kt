package ropa.miragaya.sudokupremium.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM saved_game WHERE id = 1")
    fun getSavedGame(): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(game: GameEntity)

    @Query("DELETE FROM saved_game")
    suspend fun clearSavedGame()

    @Insert
    suspend fun insertHistory(history: GameHistoryEntity)
}