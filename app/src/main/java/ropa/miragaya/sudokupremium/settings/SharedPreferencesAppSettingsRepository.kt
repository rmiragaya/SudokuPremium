package ropa.miragaya.sudokupremium.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SharedPreferencesAppSettingsRepository @Inject constructor(@ApplicationContext context: Context) :
    AppSettingsRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        AppSettings(
            hapticsEnabled = preferences.getBoolean(KEY_HAPTICS_ENABLED, true)
        )
    )
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setHapticsEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_HAPTICS_ENABLED, enabled)
            .apply()
        _settings.value = _settings.value.copy(hapticsEnabled = enabled)
    }

    private companion object {
        const val PREFERENCES_NAME = "sudoku_mentor_settings"
        const val KEY_HAPTICS_ENABLED = "haptics_enabled"
    }
}
