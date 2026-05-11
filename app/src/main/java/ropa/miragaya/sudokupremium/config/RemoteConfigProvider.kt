package ropa.miragaya.sudokupremium.config

interface RemoteConfigProvider {
    val newGameLoadingMinDurationMs: Long
    val adsEnabled: Boolean
    val techniquesEnabled: Boolean
    val freeHintsPerGame: Int
    val rewardedHintsEnabled: Boolean
    val premiumEnabled: Boolean
    val rewardedHintAdUnitId: String

    fun initialize()

    fun fetchAndActivate()
}

object RemoteConfigDefaults {
    const val NEW_GAME_LOADING_MIN_DURATION_MS = 900L
    const val ADS_ENABLED = false
    const val TECHNIQUES_ENABLED = true
    const val FREE_HINTS_PER_GAME = 3
    const val REWARDED_HINTS_ENABLED = true
    const val PREMIUM_ENABLED = true
    const val REWARDED_HINT_AD_UNIT_ID = ""
}
