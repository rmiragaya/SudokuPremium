package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.StrategyContext
import javax.inject.Inject

class HintMessageFactory @Inject constructor() {

    fun getSuccessMessage(context: StrategyContext, value: Int): String {
        return "¡Paso Final! Aplicando la lógica de ${context.name}, podemos deducir que en esta celda solo puede ir el número $value."
    }

    fun getEliminationMessage(context: StrategyContext, notesToRemoveMap: Map<Int, List<Int>>): String {
        val totalNotesRemoved = notesToRemoveMap.values.sumOf { it.size }
        val plural = if (totalNotesRemoved > 1) "candidatos" else "candidato"

        return when (context) {
            is StrategyContext.IntersectionRemoval -> {
                "Como el candidato ${context.candidateNumber} está confinado a ${context.containerType}, " +
                        "podemos eliminarlo del resto de las celdas cruzadas. Se eliminaron $totalNotesRemoved $plural."
            }
            is StrategyContext.Generic -> {
                "Usando ${context.name} podemos eliminar $totalNotesRemoved $plural."
            }
        }
    }
}