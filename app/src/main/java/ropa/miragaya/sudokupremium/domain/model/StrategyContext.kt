package ropa.miragaya.sudokupremium.domain.model

sealed class StrategyContext {
    abstract val name: String

    open fun getSuccessMessage(valueToSet: Int): String {
        return "Aplicando la lógica de $name, podemos deducir que en esta celda solo puede ir el número $valueToSet."
    }

    open fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
        val totalNotesRemoved = notesToRemoveMap.values.sumOf { it.size }
        val plural = if (totalNotesRemoved > 1) "candidatos" else "candidato"
        return "Usando $name podemos eliminar $totalNotesRemoved $plural."
    }

    sealed class IntersectionRemoval : StrategyContext() {
        override val name = "Intersection Removal"
        abstract val candidateNumber: Int

        data class Pointing(
            override val candidateNumber: Int,
            val boxIndex: Int,
            val lineType: String,
            val lineIndex: Int
        ) : IntersectionRemoval() {
            override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
                return "Observá la caja ${boxIndex + 1}. Todos los candidatos para el número $candidateNumber " +
                        "están alineados en la $lineType ${lineIndex + 1}. " +
                        "Como el $candidateNumber debe estar obligatoriamente en esa caja, " +
                        "es seguro eliminarlo como candidato del resto de la $lineType."
            }
        }

        data class BoxLineReduction(
            override val candidateNumber: Int,
            val lineType: String,
            val lineIndex: Int,
            val boxIndex: Int
        ) : IntersectionRemoval() {
            override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
                return "Observá la $lineType ${lineIndex + 1}. Los únicos lugares posibles " +
                        "para el número $candidateNumber dentro de esta $lineType caen todos " +
                        "dentro de la caja ${boxIndex + 1}. " +
                        "Por lo tanto, podemos eliminar el $candidateNumber como candidato de las demás celdas de la caja ${boxIndex + 1}."
            }
        }
    }

    data class NakedPair(
        val pairedCandidates: List<Int>,
        val containerType: String,
        val containerIndex: Int
    ) : StrategyContext() {
        override val name = "Naked Pair"

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            val num1 = pairedCandidates[0]
            val num2 = pairedCandidates[1]
            return "Observá la $containerType ${containerIndex + 1}. Hay exactamente dos celdas que solo contienen los candidatos $num1 y $num2. " +
                    "Como estos dos números deben ubicarse obligatoriamente en esas dos celdas, " +
                    "podemos eliminarlos de forma segura como candidatos en el resto de la $containerType."
        }
    }

    data class NakedSingle(
        val row: Int,
        val col: Int
    ) : StrategyContext() {
        override val name = "Naked Single"

        override fun getSuccessMessage(valueToSet: Int): String {
            return "Solo el $valueToSet puede ir en la casilla marcada (fila ${row + 1}, columna ${col + 1}), " +
                    "ya que el resto de los números ya están en su fila, columna o dentro de la caja."
        }
    }

    data class HiddenSingle(
        val row: Int,
        val col: Int,
        val value: Int,
        val regionType: String,
        val regionIndex: Int
    ) : StrategyContext() {
        override val name = "Hidden Single"

        override fun getSuccessMessage(valueToSet: Int): String {
            return "Fijate bien en la $regionType ${regionIndex + 1}. " +
                    "Si revisás los candidatos, vas a ver que, de toda la $regionType, el $value solo puede ir en la casilla marcada. " +
                    "El $value no tiene ningun otro lugar posible en esa $regionType."
        }
    }


    data class Generic(val strategyName: String) : StrategyContext() {
        override val name = strategyName
    }
}