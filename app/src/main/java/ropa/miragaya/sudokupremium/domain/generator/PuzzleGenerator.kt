package ropa.miragaya.sudokupremium.domain.generator

import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle

interface PuzzleGenerator {
    fun generate(targetDifficulty: Difficulty): SudokuPuzzle
}
