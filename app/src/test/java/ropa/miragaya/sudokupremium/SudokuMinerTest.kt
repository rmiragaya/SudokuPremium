package ropa.miragaya.sudokupremium

import ropa.miragaya.sudokupremium.domain.generator.BoardGenerator
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.solver.SolveResult
import ropa.miragaya.sudokupremium.domain.solver.Solver

class SudokuMinerTest {

    private val solver = Solver()

//    // Mina EASY
//    @Test
//    fun mineEasySeeds() {
//        mineSeeds(Difficulty.EASY, seedsNeeded = 50)
//    }
//
//    // Mina MEDIUM
//    @Test
//    fun mineMediumSeeds() {
//        mineSeeds(Difficulty.MEDIUM, seedsNeeded = 50)
//    }
//
//    // Mina HARD
//    @Test
//    fun mineHardSeeds() {
//        mineSeeds(targetDifficulty = Difficulty.HARD, seedsNeeded = 50)
//    }
//
//    // Mina EXPERT
//    @Test
//    fun mineExpertSeeds() {
//        mineSeeds(targetDifficulty = Difficulty.EXPERT, seedsNeeded = 50)
//    }

    /**
     * Lógica compartida de minería.
     * Busca tableros que coincidan EXACTAMENTE con la dificultad pedida.
     */
    private fun mineSeeds(targetDifficulty: Difficulty, seedsNeeded: Int) {
        println("⛏️ INICIANDO MINERÍA DE SEMILLAS [${targetDifficulty.name}]...")
        println("--------------------------------------------------")

        var foundCount = 0
        var attempts = 0
        val startTime = System.currentTimeMillis()

        while (foundCount < seedsNeeded) {
            attempts++

            val solvedBoard = BoardGenerator.generateFilledBoard()
            var currentBoard = solvedBoard

            val cellIndices = (0..80).toList().shuffled()
            var currentDifficulty = Difficulty.EASY

            for (index in cellIndices) {
                if (currentBoard.cells[index].value == null) continue

                val nextBoard = currentBoard.withCellValue(index, null)
                val result = solver.solve(nextBoard)

                if (result is SolveResult.Success) {
                    currentBoard = nextBoard
                    currentDifficulty = result.difficulty
                }
            }

            if (currentDifficulty == targetDifficulty) {
                foundCount++
                val seedString = currentBoard.toRawString()

                val emoji = if (targetDifficulty == Difficulty.EXPERT) "💎" else "🟠"
                println("\n$emoji ¡SEMILLA ${targetDifficulty.name} #$foundCount ENCONTRADA! (Intento $attempts)")
                println("\"$seedString\",")
            }

            if (attempts % 100 == 0) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                println("... minando ${targetDifficulty.name} ... ($attempts intentos, ${elapsed}s)")
            }
        }

        println("--------------------------------------------------")
        println("🏁 MINERÍA FINALIZADA. Copiá las semillas de arriba.")
    }

    private fun ropa.miragaya.sudokupremium.domain.model.Board.toRawString(): String {
        return this.cells.joinToString("") { it.value?.toString() ?: "0" }
    }
}
