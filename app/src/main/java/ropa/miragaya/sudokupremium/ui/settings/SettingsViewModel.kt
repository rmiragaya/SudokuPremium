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
import ropa.miragaya.sudokupremium.ui.model.PremiumStatusMessage

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val premiumEntitlementRepository: PremiumEntitlementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var didRequestPremiumAction = false

    init {
        observeSettings()
        observePremiumEntitlement()
    }

    fun onHapticsEnabledChanged(enabled: Boolean) {
        appSettingsRepository.setHapticsEnabled(enabled)
    }

    fun onPurchasePremiumClick(activity: Activity?) {
        didRequestPremiumAction = true
        if (activity == null) {
            _uiState.update { it.copy(premiumStatusMessage = PremiumStatusMessage.PURCHASE_NOT_STARTED) }
            return
        }

        premiumEntitlementRepository.launchPremiumPurchase(activity)
    }

    fun onRestorePremiumClick() {
        didRequestPremiumAction = true
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
                        premiumStatusMessage = purchaseState.toSettingsMessage(didRequestPremiumAction)
                    )
                }
            }
        }
    }
}

private fun PremiumPurchaseState.toSettingsMessage(didRequestPremiumAction: Boolean): PremiumStatusMessage? {
    return when (this) {
        PremiumPurchaseState.Purchased,
        PremiumPurchaseState.Restored -> PremiumStatusMessage.PREMIUM_ACTIVATED
        PremiumPurchaseState.Pending -> if (didRequestPremiumAction) PremiumStatusMessage.PURCHASE_PENDING else null
        PremiumPurchaseState.Canceled -> if (didRequestPremiumAction) PremiumStatusMessage.PURCHASE_CANCELED else null
        is PremiumPurchaseState.Failed -> if (didRequestPremiumAction) {
            PremiumStatusMessage.PREMIUM_ACTIVATION_FAILED
        } else {
            null
        }
        PremiumPurchaseState.Idle,
        PremiumPurchaseState.Loading -> null
    }
}
