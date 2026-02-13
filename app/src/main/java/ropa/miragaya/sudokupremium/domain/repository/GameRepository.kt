package ropa.miragaya.sudokupremium.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ropa.miragaya.sudokupremium.data.local.GameDao
import ropa.miragaya.sudokupremium.data.local.GameHistoryEntity
import ropa.miragaya.sudokupremium.data.mapper.toDomain
import ropa.miragaya.sudokupremium.data.mapper.toEntity
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import javax.inject.Inject

interface GameRepository {
    fun getSavedGame(): Flow<SavedGame?>

    suspend fun saveGame(game: SavedGame)

    suspend fun saveVictory(time: Long, difficulty: Difficulty)
}
class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao
) : GameRepository {

    override fun getSavedGame(): Flow<SavedGame?> {
        return dao.getSavedGame().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveGame(game: SavedGame) {
        dao.saveGame(game.toEntity())
    }

    override suspend fun saveVictory(time: Long, difficulty: Difficulty) {
        dao.insertHistory(
            GameHistoryEntity(
                timeSeconds = time,
                difficulty = difficulty
            )
        )

        dao.clearSavedGame()
    }
}