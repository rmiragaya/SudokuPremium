package ropa.miragaya.sudokupremium.domain.model.analytics

import ropa.miragaya.sudokupremium.domain.model.Difficulty

data class GenerationMetrics(
    val success: Boolean,
    val targetDifficulty: Difficulty,
    val actualDifficulty: Difficulty,
    val durationMs: Long,
    val boardString: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return """
            📊 GENERATION REPORT
            ----------------------------------------
            Result: ${if (success) "✅ SUCCESS" else "⚠️ APPROXIMATION"}
            Target: $targetDifficulty | Actual: $actualDifficulty
            Time: ${durationMs}ms (${durationMs / 1000}s)
            ----------------------------------------
        """.trimIndent()
    }
}