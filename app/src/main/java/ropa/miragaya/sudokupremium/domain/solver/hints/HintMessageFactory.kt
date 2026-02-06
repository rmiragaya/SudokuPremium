package ropa.miragaya.sudokupremium.domain.solver.hints

import javax.inject.Inject

class HintMessageFactory @Inject constructor() {

    fun getSuccessMessage(strategyName: String, value: Int): String {
        return when (strategyName) {
            "Naked Single" -> "El $value es el único candidato posible en esta celda. Los demás números ya están presentes en su fila, columna o caja."
            "Hidden Single" -> "El $value solo puede ir en esta posición dentro de su sector (fila, columna o caja), aunque la celda tenga otras notas candidatas."
            else -> "Lógica de $strategyName: Esta celda debe ser un $value."
        }
    }

    fun getEliminationMessage(strategyName: String, notesRemoved: List<Int>, notesKept: List<Int>): String {
        val removedStr = notesRemoved.joinToString(", ")
        val keptStr = notesKept.joinToString(" y ")
        return when (strategyName) {
            "Naked Pair" -> "Pareja Desnuda: Hay dos celdas en este sector que solo tienen los números ($removedStr). Por eso, esos números no pueden ir aquí."
            "Hidden Pair" -> "Pareja Oculta: Los números $keptStr solo aparecen en estas dos celdas dentro del sector. Por seguridad, eliminamos el resto de las notas ($removedStr)."
            "Intersection Removal" -> "Intersección: Los candidatos ($removedStr) están alineados obligatoriamente en otra parte de la caja, así que no pueden ir aquí."
            "Naked Triple" -> "Trío Desnudo: Tres celdas comparten tres números exclusivos. Eliminamos ($removedStr) del resto del sector."
            "X-Wing" -> "X-Wing: El número ${notesRemoved.firstOrNull() ?: "?"} forma un rectángulo perfecto en otro lado, lo que impide que vaya aquí."
            "Y-Wing" -> "Y-Wing: El número ${notesRemoved.firstOrNull() ?: "?"} generaría una contradicción lógica debido a la conexión entre tres celdas bi-valor."
            else -> "Estrategia $strategyName: Las notas $removedStr no son posibles aquí."
        }
    }
}