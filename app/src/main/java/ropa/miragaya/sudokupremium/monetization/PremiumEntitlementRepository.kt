package ropa.miragaya.sudokupremium.monetization

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PremiumEntitlementRepository {
    val isPremium: StateFlow<Boolean>
    val purchaseState: StateFlow<PremiumPurchaseState>

    fun refreshPurchases()

    fun launchPremiumPurchase(activity: Activity)

    fun resetPremiumForDebug()
}

sealed interface PremiumPurchaseState {
    data object Idle : PremiumPurchaseState
    data object Loading : PremiumPurchaseState
    data object Purchased : PremiumPurchaseState
    data object Restored : PremiumPurchaseState
    data object Pending : PremiumPurchaseState
    data object Canceled : PremiumPurchaseState
    data class Failed(val reason: String?) : PremiumPurchaseState
}
