package ropa.miragaya.sudokupremium.domain.solver.hints

import ropa.miragaya.sudokupremium.domain.model.StrategyContext
import javax.inject.Inject

class HintMessageFactory @Inject constructor() {

    fun getSuccessMessage(context: StrategyContext, value: Int): String {
        return "Aplicando la lógica de ${context.name}, podemos deducir que en esta celda solo puede ir el número $value."
    }

    fun getEliminationMessage(context: StrategyContext, notesToRemoveMap: Map<Int, List<Int>>): String {
        val totalNotesRemoved = notesToRemoveMap.values.sumOf { it.size }
        val plural = if (totalNotesRemoved > 1) "candidatos" else "candidato"

        return when (context) {
            is StrategyContext.IntersectionRemoval.Pointing -> {
                "Observá la caja ${context.boxIndex + 1}. Todos los candidatos para el número ${context.candidateNumber} " +
                        "están alineados en la ${context.lineType} ${context.lineIndex + 1}. " +
                        "Como el ${context.candidateNumber} debe estar obligatoriamente en esa caja, " +
                        "podemos deducir que ocupará una de esas celdas alineadas. " +
                        "Por lo tanto, es seguro eliminar el ${context.candidateNumber} como candidato del resto de la ${context.lineType}."
            }
            is StrategyContext.IntersectionRemoval.BoxLineReduction -> {
                "Observá la ${context.lineType} ${context.lineIndex + 1}. Los únicos lugares posibles " +
                        "para el número ${context.candidateNumber} dentro de esta ${context.lineType} caen todos " +
                        "dentro de la caja ${context.boxIndex + 1}. " +
                        "Como la ${context.lineType} necesita obligatoriamente un ${context.candidateNumber}, este deberá " +
                        "ubicarse dentro de esa caja específica. " +
                        "Por lo tanto, podemos eliminar el ${context.candidateNumber} como candidato de las demás celdas de la caja ${context.boxIndex + 1}."
            }

            is StrategyContext.NakedPair -> {
                val num1 = context.pairedCandidates[0]
                val num2 = context.pairedCandidates[1]

                "Observá la ${context.containerType} ${context.containerIndex + 1}. " +
                        "Hay exactamente dos celdas que solo contienen los candidatos $num1 y $num2. " +
                        "Como estos dos números deben ubicarse obligatoriamente en esas dos celdas (aunque no sepamos en qué orden), " +
                        "podemos eliminarlos de forma segura como candidatos en el resto de la ${context.containerType}."
            }

            is StrategyContext.Generic -> {
                "Usando ${context.name} podemos eliminar $totalNotesRemoved $plural."
            }
        }
    }
}