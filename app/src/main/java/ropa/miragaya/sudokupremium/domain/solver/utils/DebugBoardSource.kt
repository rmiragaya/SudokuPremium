package ropa.miragaya.sudokupremium.domain.solver.utils

import ropa.miragaya.sudokupremium.domain.model.Board

interface DebugBoardSource {
    fun loadBoardFromJson(fileName: String): Board?

    fun loadBoardFromGrid(): Board?
}
