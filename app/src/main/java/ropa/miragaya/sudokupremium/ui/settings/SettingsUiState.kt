package ropa.miragaya.sudokupremium.ui.settings

data class SettingsUiState(
    val hapticsEnabled: Boolean = true,
    val isPremium: Boolean = false,
    val isPurchaseLoading: Boolean = false,
    val premiumStatusMessage: String? = null
)
