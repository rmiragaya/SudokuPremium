package ropa.miragaya.sudokupremium.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.domain.model.Difficulty

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
    AnalyticsTracker {

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun logScreenViewed(screenName: String) {
        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                FirebaseAnalytics.Param.SCREEN_CLASS to screenName
            )
        )
    }

    override fun logDifficultySelected(difficulty: Difficulty) {
        logEvent(
            name = EVENT_DIFFICULTY_SELECTED,
            PARAM_DIFFICULTY to difficulty.analyticsValue()
        )
    }

    override fun logContinueGameSelected() {
        logEvent(name = EVENT_CONTINUE_GAME_SELECTED)
    }

    override fun logNewGameStarted(difficulty: Difficulty) {
        logEvent(
            name = EVENT_NEW_GAME_STARTED,
            PARAM_DIFFICULTY to difficulty.analyticsValue()
        )
    }

    override fun logGameCompleted(difficulty: Difficulty, elapsedSeconds: Long, hintsUsed: Int, mistakesRevealed: Int) {
        logEvent(
            name = EVENT_GAME_COMPLETED,
            PARAM_DIFFICULTY to difficulty.analyticsValue(),
            PARAM_ELAPSED_SECONDS to elapsedSeconds,
            PARAM_HINTS_USED to hintsUsed,
            PARAM_MISTAKES_REVEALED to mistakesRevealed
        )
    }

    override fun logHintRequested(difficulty: Difficulty, elapsedSeconds: Long, hasMistakes: Boolean) {
        logEvent(
            name = EVENT_HINT_REQUESTED,
            PARAM_DIFFICULTY to difficulty.analyticsValue(),
            PARAM_ELAPSED_SECONDS to elapsedSeconds,
            PARAM_HAS_MISTAKES to hasMistakes
        )
    }

    override fun logHintShown(difficulty: Difficulty, strategyName: String, hintCount: Int) {
        logEvent(
            name = EVENT_HINT_SHOWN,
            PARAM_DIFFICULTY to difficulty.analyticsValue(),
            PARAM_STRATEGY to strategyName,
            PARAM_HINT_COUNT to hintCount
        )
    }

    override fun logTechniqueOpened(techniqueId: String, source: TechniqueOpenSource) {
        logEvent(
            name = EVENT_TECHNIQUE_OPENED,
            PARAM_TECHNIQUE_ID to techniqueId,
            PARAM_SOURCE to source.analyticsValue
        )
    }

    private fun logEvent(name: String, vararg params: Pair<String, Any>) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putLong(key, value.toLong())
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putDouble(key, value.toDouble())
                is Boolean -> bundle.putLong(key, if (value) 1L else 0L)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    private fun Difficulty.analyticsValue(): String = name.lowercase()

    private companion object {
        const val EVENT_DIFFICULTY_SELECTED = "difficulty_selected"
        const val EVENT_CONTINUE_GAME_SELECTED = "continue_game_selected"
        const val EVENT_NEW_GAME_STARTED = "new_game_started"
        const val EVENT_GAME_COMPLETED = "game_completed"
        const val EVENT_HINT_REQUESTED = "hint_requested"
        const val EVENT_HINT_SHOWN = "hint_shown"
        const val EVENT_TECHNIQUE_OPENED = "technique_opened"

        const val PARAM_DIFFICULTY = "difficulty"
        const val PARAM_ELAPSED_SECONDS = "elapsed_seconds"
        const val PARAM_HINTS_USED = "hints_used"
        const val PARAM_MISTAKES_REVEALED = "mistakes_revealed"
        const val PARAM_HAS_MISTAKES = "has_mistakes"
        const val PARAM_STRATEGY = "strategy"
        const val PARAM_HINT_COUNT = "hint_count"
        const val PARAM_TECHNIQUE_ID = "technique_id"
        const val PARAM_SOURCE = "source"
    }
}
