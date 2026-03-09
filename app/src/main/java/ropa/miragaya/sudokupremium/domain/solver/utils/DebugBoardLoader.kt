package ropa.miragaya.sudokupremium.domain.solver.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Board.Companion.fromGridString
import javax.inject.Inject

class DebugBoardLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadBoardFromJson(fileName: String): Board? {
        return try {
            val rawString = context.assets
                .open("debug_boards/$fileName")
                .bufferedReader()
                .use { it.readText() }

            val jsonString = rawString.substringAfter("-- JSON --").trim()

            Gson().fromJson(jsonString, Board::class.java)
        } catch (e: Exception) {
            Log.e("DebugBoardLoader", "Error leyendo el tablero $fileName", e)
            null
        }
    }

    fun loadBoardFromGrid(): Board? {
        return try {
            fromGridString("400000938032094100095300240370609004529001673604703090957008300003900400240030709")

        } catch (e: Exception) {
            Log.e("DebugBoardLoader", "Error fromGridString", e)
            null
        }
    }
}