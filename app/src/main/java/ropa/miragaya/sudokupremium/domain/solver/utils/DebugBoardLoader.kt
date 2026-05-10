package ropa.miragaya.sudokupremium.domain.solver.utils

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Board.Companion.fromGridString

class DebugBoardLoader @Inject constructor(@param:ApplicationContext private val context: Context) : DebugBoardSource {
    override fun loadBoardFromJson(fileName: String): Board? {
        return try {
            val rawString = context.assets
                .open("debug_boards/$fileName")
                .bufferedReader()
                .use { it.readText() }

            val jsonString = rawString.substringAfter("-- JSON --").trim()

            Gson().fromJson(jsonString, Board::class.java)
        } catch (_: Exception) {
            null
        }
    }

    override fun loadBoardFromGrid(): Board? {
        return try {
            fromGridString("080090030030000069902063158020804590851907046394605870563040987200000015010050020")
        } catch (_: Exception) {
            null
        }
    }
}
