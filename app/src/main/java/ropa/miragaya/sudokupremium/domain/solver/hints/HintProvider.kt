package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuHint

interface HintProvider {
    fun findAllHints(board: Board): List<SudokuHint>
}
