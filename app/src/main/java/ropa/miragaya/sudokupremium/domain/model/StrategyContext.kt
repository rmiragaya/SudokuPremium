package ropa.miragaya.sudokupremium.domain.model

sealed class StrategyContext {
    val name: String get() = when(this) {
        is IntersectionRemoval -> "Intersection Removal"
        is Generic -> this.strategyName
    }

    data class IntersectionRemoval(
        val candidateNumber: Int,
        val containerType: String
    ) : StrategyContext()

    data class Generic(val strategyName: String) : StrategyContext()
}