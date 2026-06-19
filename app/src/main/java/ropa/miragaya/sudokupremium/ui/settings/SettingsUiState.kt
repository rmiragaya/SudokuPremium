package ropa.miragaya.sudokupremium.ui.settings

import ropa.miragaya.sudokupremium.ui.model.PremiumStatusMessage

data class SettingsUiState(
    val hapticsEnabled: Boolean = true,
    val supportCode: String = "",
    val isPremium: Boolean = false,
    val isPurchaseLoading: Boolean = false,
    val premiumStatusMessage: PremiumStatusMessage? = null
)
