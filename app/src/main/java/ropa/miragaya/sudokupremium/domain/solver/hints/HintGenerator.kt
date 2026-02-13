package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.domain.model.initializeCandidates
import ropa.miragaya.sudokupremium.domain.solver.Solver
import javax.inject.Inject

class HintGenerator @Inject constructor(
    private val solver: Solver,
    private val messageFactory: HintMessageFactory
) {

    fun findAllHints(board: Board): List<SudokuHint> {
        val finalHint = findDeepNumberHint(board)

        if (finalHint != null) {
            return listOf(finalHint)
        } else {
            return emptyList()
        }
    }

    private fun findDeepNumberHint(board: Board): SudokuHint? {
        var currentBoard = board.initializeCandidates()
        val strategies = solver.strategies


        val explanationSteps = mutableListOf<String>()
        var firstCleanupHint: SudokuHint? = null

        var safetyCounter = 20

        while (safetyCounter > 0) {
            var progressMade = false

            for (strategy in strategies) {

                val boardsFound = strategy.findAll(currentBoard)

                if (boardsFound.isNotEmpty()) {

                    val newBoard = boardsFound.first()
                    val hintDiff = createHintFromDiff(strategy.name, currentBoard, newBoard)

                    if (hintDiff != null) {
                        if (hintDiff.value != null) {

                            val finalDesc = buildString {
                                if (explanationSteps.isNotEmpty()) {
                                    append("Para llegar a este número, deducciones previas:\n")
                                    explanationSteps.forEach { append("• $it\n") }
                                    append("\n👉 Finalmente: ")
                                }
                                append(hintDiff.description)
                            }
                            return hintDiff.copy(description = finalDesc.trim())
                        } else {

                            if (firstCleanupHint == null) {
                                firstCleanupHint = hintDiff
                            }
                            explanationSteps.add(hintDiff.description)
                            currentBoard = newBoard
                            progressMade = true
                            break // Rompe el for, vuelve al while
                        }
                    } else {
                        println("[HINT_DEBUG] ERROR RARO: '${strategy.name}' devolvió un tablero pero el Diff no encontró diferencias.")
                    }
                }
            }

            if (!progressMade) {
                println("[HINT_DEBUG] NINGUNA estrategia pudo avanzar en este ciclo. El Solver está atascado.")
                break
            }
        }

        if (safetyCounter == 0) {
            println("[HINT_DEBUG] ADVERTENCIA: Se alcanzó el límite de $safetyCounter ciclos. Posible bucle infinito.")
        }

        if (firstCleanupHint != null) {
            println("[HINT_DEBUG] Devolviendo pista de limpieza de emergencia (Fallback).")
        }

        return firstCleanupHint
    }

    private fun createHintFromDiff(strategyName: String, oldBoard: Board, newBoard: Board): SudokuHint? {
        for (i in 0 until 81) {
            val oldCell = oldBoard.cells[i]
            val newCell = newBoard.cells[i]

            if (oldCell.value != newCell.value && newCell.value != null) {
                return SudokuHint(
                    strategyName = strategyName,
                    description = messageFactory.getSuccessMessage(strategyName, newCell.value),
                    row = oldCell.row,
                    col = oldCell.col,
                    value = newCell.value,
                    notesRemoved = emptyList()
                )
            }

            if (oldCell.notes != newCell.notes) {
                val removed = (oldCell.notes - newCell.notes).toList()
                val kept = newCell.notes.toList()

                if (removed.isNotEmpty()) {
                    return SudokuHint(
                        strategyName = strategyName,
                        description = messageFactory.getEliminationMessage(strategyName, removed, kept),
                        row = oldCell.row,
                        col = oldCell.col,
                        value = null,
                        notesRemoved = removed
                    )
                }
            }
        }
        return null
    }
}