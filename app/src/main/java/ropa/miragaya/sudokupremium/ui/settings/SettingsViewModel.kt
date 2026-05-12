package ropa.miragaya.sudokupremium.ui.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.monetization.PremiumEntitlementRepository
import ropa.miragaya.sudokupremium.monetization.PremiumPurchaseState
import ropa.miragaya.sudokupremium.settings.AppSettingsRepository

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val premiumEntitlementRepository: PremiumEntitlementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
        observePremiumEntitlement()
    }

    fun onHapticsEnabledChanged(enabled: Boolean) {
        appSettingsRepository.setHapticsEnabled(enabled)
    }

    fun onPurchasePremiumClick(activity: Activity?) {
        if (activity == null) {
            _uiState.update { it.copy(premiumStatusMessage = "No se pudo iniciar la compra.") }
            return
        }

        premiumEntitlementRepository.launchPremiumPurchase(activity)
    }

    fun onRestorePremiumClick() {
        premiumEntitlementRepository.refreshPurchases()
    }

    fun onDebugResetPremiumClick() {
        premiumEntitlementRepository.resetPremiumForDebug()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            appSettingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(hapticsEnabled = settings.hapticsEnabled) }
            }
        }
    }

    private fun observePremiumEntitlement() {
        viewModelScope.launch {
            premiumEntitlementRepository.isPremium.collect { isPremium ->
                _uiState.update {
                    it.copy(
                        isPremium = isPremium,
                        premiumStatusMessage = if (isPremium) null else it.premiumStatusMessage
                    )
                }
            }
        }

        viewModelScope.launch {
            premiumEntitlementRepository.purchaseState.collect { purchaseState ->
                _uiState.update {
                    it.copy(
                        isPurchaseLoading = purchaseState == PremiumPurchaseState.Loading,
                        premiumStatusMessage = purchaseState.toSettingsMessage()
                    )
                }
            }
        }
    }
}

private fun PremiumPurchaseState.toSettingsMessage(): String? {
    return when (this) {
        PremiumPurchaseState.Purchased,
        PremiumPurchaseState.Restored -> "Premium activado."
        PremiumPurchaseState.Pending -> "La compra quedó pendiente."
        PremiumPurchaseState.Canceled -> "Compra cancelada."
        is PremiumPurchaseState.Failed -> "No se pudo activar Premium."
        PremiumPurchaseState.Idle,
        PremiumPurchaseState.Loading -> null
    }
}
