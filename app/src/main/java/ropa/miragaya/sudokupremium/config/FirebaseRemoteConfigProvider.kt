package ropa.miragaya.sudokupremium.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.BuildConfig

@Singleton
class FirebaseRemoteConfigProvider @Inject constructor(private val remoteConfig: FirebaseRemoteConfig) :
    RemoteConfigProvider {

    override val newGameLoadingMinDurationMs: Long
        get() = remoteConfig
            .getLong(KEY_NEW_GAME_LOADING_MIN_DURATION_MS)
            .coerceIn(MIN_LOADING_DURATION_MS, MAX_LOADING_DURATION_MS)

    override val adsEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_ADS_ENABLED)

    override val techniquesEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_TECHNIQUES_ENABLED)

    override fun initialize() {
        val minimumFetchInterval = if (BuildConfig.DEBUG) {
            DEBUG_MINIMUM_FETCH_INTERVAL_SECONDS
        } else {
            RELEASE_MINIMUM_FETCH_INTERVAL_SECONDS
        }

        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(minimumFetchInterval)
            .build()

        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(
            mapOf(
                KEY_NEW_GAME_LOADING_MIN_DURATION_MS to RemoteConfigDefaults.NEW_GAME_LOADING_MIN_DURATION_MS,
                KEY_ADS_ENABLED to RemoteConfigDefaults.ADS_ENABLED,
                KEY_TECHNIQUES_ENABLED to RemoteConfigDefaults.TECHNIQUES_ENABLED
            )
        )
    }

    override fun fetchAndActivate() {
        remoteConfig.fetchAndActivate()
    }

    private companion object {
        const val KEY_NEW_GAME_LOADING_MIN_DURATION_MS = "new_game_loading_min_duration_ms"
        const val KEY_ADS_ENABLED = "ads_enabled"
        const val KEY_TECHNIQUES_ENABLED = "techniques_enabled"

        const val DEBUG_MINIMUM_FETCH_INTERVAL_SECONDS = 60L
        const val RELEASE_MINIMUM_FETCH_INTERVAL_SECONDS = 12 * 60 * 60L

        const val MIN_LOADING_DURATION_MS = 300L
        const val MAX_LOADING_DURATION_MS = 2_500L
    }
}
