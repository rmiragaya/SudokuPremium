package ropa.miragaya.sudokupremium.config

interface RemoteConfigProvider {
    val newGameLoadingMinDurationMs: Long
    val adsEnabled: Boolean
    val techniquesEnabled: Boolean

    fun initialize()

    fun fetchAndActivate()
}

object RemoteConfigDefaults {
    const val NEW_GAME_LOADING_MIN_DURATION_MS = 900L
    const val ADS_ENABLED = false
    const val TECHNIQUES_ENABLED = true
}
