package ropa.miragaya.sudokupremium.analytics

import ropa.miragaya.sudokupremium.domain.model.Difficulty

interface AnalyticsTracker {
    fun logScreenViewed(screenName: String)

    fun logDifficultySelected(difficulty: Difficulty)

    fun logContinueGameSelected()

    fun logNewGameStarted(difficulty: Difficulty)

    fun logGameCompleted(difficulty: Difficulty, elapsedSeconds: Long, hintsUsed: Int, mistakesRevealed: Int)

    fun logHintRequested(difficulty: Difficulty, elapsedSeconds: Long, hasMistakes: Boolean)

    fun logHintShown(difficulty: Difficulty, strategyName: String, hintCount: Int)

    fun logTechniqueOpened(techniqueId: String, source: TechniqueOpenSource)
}

enum class TechniqueOpenSource(val analyticsValue: String) {
    LIBRARY("library"),
    HINT("hint")
}
