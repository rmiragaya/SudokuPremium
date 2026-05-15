package ropa.miragaya.sudokupremium.settings

import kotlinx.coroutines.flow.StateFlow

interface AppSettingsRepository {
    val settings: StateFlow<AppSettings>

    fun setHapticsEnabled(enabled: Boolean)

    fun setHasStartedAnyGame(started: Boolean)

    fun setHowToPlayTutorialSeen(seen: Boolean)
}
