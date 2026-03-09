package ropa.miragaya.sudokupremium.domain.model

sealed class StrategyContext {
    val name: String get() = when(this) {
        is IntersectionRemoval -> "Intersection Removal"
        is NakedPair -> "Naked Pair"
        is Generic -> this.strategyName
    }

    sealed class IntersectionRemoval : StrategyContext() {
        abstract val candidateNumber: Int

        data class Pointing(
            override val candidateNumber: Int,
            val boxIndex: Int,
            val lineType: String,
            val lineIndex: Int
        ) : IntersectionRemoval()

        data class BoxLineReduction(
            override val candidateNumber: Int,
            val lineType: String,
            val lineIndex: Int,
            val boxIndex: Int
        ) : IntersectionRemoval()
    }

    data class NakedPair(
        val pairedCandidates: List<Int>,
        val containerType: String,
        val containerIndex: Int
    ) : StrategyContext()

    data class Generic(val strategyName: String) : StrategyContext()
}