package ropa.miragaya.sudokupremium.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ropa.miragaya.sudokupremium.data.local.GameDao
import ropa.miragaya.sudokupremium.data.local.GameEntity
import ropa.miragaya.sudokupremium.domain.model.SavedGame
import javax.inject.Inject

interface GameRepository {
    // Observamos la partida guardada (Flow nos avisa si cambia)
    fun getSavedGame(): Flow<SavedGame?>

    // Guardamos el estado actual
    suspend fun saveGame(game: SavedGame)
}

class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao
) : GameRepository {

    override fun getSavedGame(): Flow<SavedGame?> {
        return dao.getSavedGame().map { entity ->
            entity?.let {
                SavedGame(
                    board = it.board,
                    elapsedTimeSeconds = it.elapsedTimeSeconds,
                    difficulty = it.difficulty
                )
            }
        }
    }

    override suspend fun saveGame(game: SavedGame) {
        val entity = GameEntity(
            board = game.board,
            elapsedTimeSeconds = game.elapsedTimeSeconds,
            difficulty = game.difficulty
        )
        dao.saveGame(entity)
    }
}