package ropa.miragaya.sudokupremium.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromBoard(board: Board): String {
        return gson.toJson(board)
    }

    @TypeConverter
    fun toBoard(json: String): Board {
        return gson.fromJson(json, Board::class.java)
    }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(name: String): Difficulty {
        return try {
            Difficulty.valueOf(name)
        } catch (e: Exception) {
            Difficulty.MEDIUM
        }
    }
}