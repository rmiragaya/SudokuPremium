package ropa.miragaya.sudokupremium.domain.solver.hints

import javax.inject.Inject

class HintMessageFactory @Inject constructor() {

    fun getSuccessMessage(strategyName: String, value: Int): String {
        return "¡Paso Final! Aplicando la lógica de $strategyName, podemos deducir que en esta celda solo puede ir el número $value."
    }

    fun getEliminationMessage(strategyName: String, notesToRemoveMap: Map<Int, List<Int>>): String {
        // Podés hacer esto tan complejo o simple como quieras.
        // Por ahora, unificamos el mensaje para que sea claro.
        val totalNotesRemoved = notesToRemoveMap.values.sumOf { it.size }
        val plural = if (totalNotesRemoved > 1) "candidatos" else "candidato"

        return "Paso intermedio: Usando $strategyName podemos eliminar $totalNotesRemoved $plural de las celdas resaltadas. Esto nos acercará a la solución."
    }
}