package ropa.miragaya.sudokupremium.domain.generator

import javax.inject.Inject
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuPuzzle
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver

class SudokuGenerator @Inject constructor(private val solver: Solver, private val transformer: SudokuTransformer) :
    PuzzleGenerator {

    override fun generate(targetDifficulty: Difficulty): SudokuPuzzle {
        val seeds = seedsFor(targetDifficulty)

        if (seeds.isEmpty()) throw IllegalStateException("Faltan semillas para $targetDifficulty")

        var lastActualDifficulty: Difficulty? = null

        seeds.shuffled().forEach { seed ->
            val baseBoard = Board.fromGridString(seed)
            val newBoard = transformer.transform(baseBoard)

            when (val solvedResult = solver.solve(newBoard)) {
                is SolveResult.Success -> {
                    lastActualDifficulty = solvedResult.difficulty
                    if (solvedResult.difficulty == targetDifficulty) {
                        val puzzle = SudokuPuzzle(newBoard, solvedResult.board, targetDifficulty)
                        return puzzle
                    }
                }
                is SolveResult.Failure,
                SolveResult.Invalid -> Unit
            }
        }

        throw IllegalStateException(
            "No se pudo generar un sudoku $targetDifficulty. " +
                "Ultima dificultad resuelta: ${lastActualDifficulty ?: "sin solucion"}"
        )
    }

    private fun seedsFor(targetDifficulty: Difficulty): List<String> {
        return when (targetDifficulty) {
            Difficulty.EASY -> Seeds.EASY_SEEDS
            Difficulty.MEDIUM -> Seeds.MEDIUM_SEEDS
            Difficulty.HARD -> Seeds.HARD_SEEDS
            Difficulty.EXPERT -> Seeds.EXPERT_SEEDS
        }
    }
}
