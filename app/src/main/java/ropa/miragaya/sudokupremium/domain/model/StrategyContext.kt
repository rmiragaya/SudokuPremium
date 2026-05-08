package ropa.miragaya.sudokupremium.domain.model

sealed class StrategyContext {
    abstract val name: String
    open val highlightCellIds: List<Int> = emptyList()
    open val highlightBoxIndexes: List<Int> = emptyList()

    open fun getSuccessMessage(valueToSet: Int): String {
        return "Aplicando $name, podemos deducir que en la casilla resaltada solo puede ir el número $valueToSet."
    }

    open fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
        val totalNotesRemoved = notesToRemoveMap.values.sumOf { it.size }
        val affectedCells = notesToRemoveMap.size
        val pluralCandidates = if (totalNotesRemoved == 1) "candidato" else "candidatos"
        val pluralCells = if (affectedCells == 1) "casilla" else "casillas"

        return "Usando $name podemos eliminar $totalNotesRemoved $pluralCandidates en $affectedCells $pluralCells."
    }

    protected fun cellLabel(cellId: Int): String {
        val row = cellId / 9 + 1
        val col = cellId % 9 + 1
        return "fila $row, columna $col"
    }

    protected fun cellsLabel(cellIds: Iterable<Int>): String {
        return formatList(cellIds.map { cellLabel(it) })
    }

    protected fun cellsInContainerLabel(containerType: String, cellIds: Iterable<Int>): String {
        val ids = cellIds.toList()
        return when (containerType) {
            "fila" -> "en las columnas ${indexesLabel(ids.map { it % 9 })}"
            "columna" -> "en las filas ${indexesLabel(ids.map { it / 9 })}"
            else -> "en las casillas de ${cellsLabel(ids)}"
        }
    }

    protected fun containerLabel(containerType: String, containerIndex: Int): String {
        return "$containerType ${containerIndex + 1}"
    }

    protected fun numbersLabel(numbers: Iterable<Int>): String {
        return formatList(numbers.toSet().sorted().map { it.toString() })
    }

    protected fun indexesLabel(indices: Iterable<Int>): String {
        return numbersLabel(indices.map { it + 1 })
    }

    private fun formatList(items: List<String>): String {
        return when (items.size) {
            0 -> ""
            1 -> items.first()
            2 -> "${items[0]} y ${items[1]}"
            else -> items.dropLast(1).joinToString(", ") + " y " + items.last()
        }
    }

    data class NakedSingle(val cellId: Int) : StrategyContext() {
        override val name = "Naked Single"
        override val highlightCellIds = listOf(cellId)

        override fun getSuccessMessage(valueToSet: Int): String {
            return "La casilla resaltada en amarillo solo tiene un candidato posible: $valueToSet. " +
                "Como todos los demás números ya quedan descartados por su fila, columna o caja, esa casilla debe ser $valueToSet."
        }
    }

    data class HiddenSingle(
        val candidateNumber: Int,
        val containerType: String,
        val containerIndex: Int,
        val cellId: Int
    ) : StrategyContext() {
        override val name = "Hidden Single"
        override val highlightCellIds = listOf(cellId)
        override val highlightBoxIndexes = if (containerType == "caja") listOf(containerIndex) else emptyList()

        override fun getSuccessMessage(valueToSet: Int): String {
            return "Fijate en la ${containerLabel(containerType, containerIndex)}. " +
                "Si revisás los candidatos, el número $candidateNumber solo puede ir " +
                "en la casilla resaltada en amarillo. " +
                "No tiene ningún otro lugar posible en esa $containerType, así que esa casilla debe ser $valueToSet."
        }
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
            override val highlightBoxIndexes = listOf(boxIndex)

            override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
                return "En la caja ${boxIndex + 1}, todos los lugares posibles para el número $candidateNumber " +
                    "caen sobre la ${containerLabel(lineType, lineIndex)}. " +
                    "Entonces el $candidateNumber debe quedar dentro de esa caja, y se puede eliminar " +
                    "de las casillas marcadas en rojo."
            }
        }

        data class BoxLineReduction(
            override val candidateNumber: Int,
            val lineType: String,
            val lineIndex: Int,
            val boxIndex: Int
        ) : IntersectionRemoval() {
            override val highlightBoxIndexes = listOf(boxIndex)

            override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
                return "En la ${containerLabel(
                    lineType,
                    lineIndex
                )}, los únicos lugares posibles para el número $candidateNumber " +
                    "están todos dentro de la caja ${boxIndex + 1}. " +
                    "Por eso el $candidateNumber no puede ir en las casillas marcadas en rojo."
            }
        }
    }

    data class NakedPair(
        val pairedCandidates: List<Int>,
        val containerType: String,
        val containerIndex: Int,
        val pairCellIds: List<Int>
    ) : StrategyContext() {
        override val name = "Naked Pair"
        override val highlightCellIds = pairCellIds
        override val highlightBoxIndexes = if (containerType == "caja") listOf(containerIndex) else emptyList()

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            return "En la ${containerLabel(containerType, containerIndex)}, las dos casillas resaltadas en amarillo " +
                "tienen exactamente los mismos dos candidatos: " +
                "${numbersLabel(pairedCandidates)}. " +
                "Esos dos números quedan reservados para esas dos casillas, así que no pueden aparecer " +
                "como candidatos en las casillas marcadas en rojo."
        }
    }

    data class HiddenPair(
        val pairedCandidates: List<Int>,
        val containerType: String,
        val containerIndex: Int,
        val pairCellIds: List<Int>
    ) : StrategyContext() {
        override val name = "Hidden Pair"
        override val highlightCellIds = pairCellIds
        override val highlightBoxIndexes = if (containerType == "caja") listOf(containerIndex) else emptyList()

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            return "En la ${containerLabel(
                containerType,
                containerIndex
            )}, los números ${numbersLabel(pairedCandidates)} " +
                "solo pueden ir en las dos casillas resaltadas en amarillo. " +
                "Como esas dos casillas quedan reservadas para ese par, podemos quitar de ellas los demás candidatos."
        }
    }

    data class NakedTriple(
        val tripleCandidates: List<Int>,
        val containerType: String,
        val containerIndex: Int,
        val tripleCellIds: List<Int>
    ) : StrategyContext() {
        override val name = "Naked Triple"
        override val highlightCellIds = tripleCellIds
        override val highlightBoxIndexes = if (containerType == "caja") listOf(containerIndex) else emptyList()

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            return "En la ${containerLabel(containerType, containerIndex)}, las tres casillas resaltadas en amarillo " +
                "solo pueden contener los números " +
                "${numbersLabel(tripleCandidates)}. " +
                "Esos tres números quedan encerrados en esas tres casillas, así que se pueden eliminar " +
                "de las casillas marcadas en rojo."
        }
    }

    data class XWing(
        val candidateNumber: Int,
        val baseLineType: String,
        val baseLineIndices: List<Int>,
        val coverLineType: String,
        val coverLineIndices: List<Int>
    ) : StrategyContext() {
        override val name = "X-Wing"
        override val highlightCellIds: List<Int> = if (baseLineType == "fila") {
            baseLineIndices.flatMap { row -> coverLineIndices.map { col -> row * 9 + col } }
        } else {
            baseLineIndices.flatMap { col -> coverLineIndices.map { row -> row * 9 + col } }
        }

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            return "Las casillas resaltadas en amarillo forman un X-Wing para el número $candidateNumber. " +
                "Ese número debe ocupar dos esquinas opuestas del rectángulo, " +
                "así que puede eliminarse de las casillas marcadas en rojo."
        }
    }

    data class YWing(
        val candidateToRemove: Int,
        val pivotCellId: Int,
        val wingCellIds: List<Int>,
        val pivotCandidates: List<Int>
    ) : StrategyContext() {
        override val name = "Y-Wing"
        override val highlightCellIds = listOf(pivotCellId) + wingCellIds

        override fun getEliminationMessage(notesToRemoveMap: Map<Int, List<Int>>): String {
            return "Las casillas resaltadas en amarillo forman un Y-Wing. " +
                "La casilla pivote tiene dos opciones: ${numbersLabel(pivotCandidates)}, y las alas fuerzan " +
                "que una de ellas contenga el número $candidateToRemove. " +
                "Cualquier casilla marcada en rojo ve ambas alas, así que no puede contener ese candidato."
        }
    }

    data class Generic(val strategyName: String) : StrategyContext() {
        override val name = strategyName
    }
}
