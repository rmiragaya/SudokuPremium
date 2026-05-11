package ropa.miragaya.sudokupremium.data.mapper

import ropa.miragaya.sudokupremium.data.local.GameEntity
import ropa.miragaya.sudokupremium.domain.model.SavedGame

fun GameEntity.toDomain(): SavedGame {
    return SavedGame(
        board = this.board,
        solvedBoard = this.solvedBoard,
        elapsedTimeSeconds = this.elapsedTimeSeconds,
        difficulty = this.difficulty,
        hintsUsed = this.hintsUsed,
        rewardedHintsAvailable = this.rewardedHintsAvailable
    )
}

fun SavedGame.toEntity(): GameEntity {
    return GameEntity(
        id = 1,
        board = this.board,
        solvedBoard = this.solvedBoard,
        elapsedTimeSeconds = this.elapsedTimeSeconds,
        difficulty = this.difficulty,
        hintsUsed = this.hintsUsed,
        rewardedHintsAvailable = this.rewardedHintsAvailable,
        lastPlayed = System.currentTimeMillis()
    )
}
