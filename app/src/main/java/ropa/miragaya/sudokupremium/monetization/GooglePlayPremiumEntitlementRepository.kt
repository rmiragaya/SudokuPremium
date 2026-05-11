package ropa.miragaya.sudokupremium.monetization

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider
import ropa.miragaya.sudokupremium.crash.CrashReporter

@Singleton
class GooglePlayPremiumEntitlementRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val remoteConfigProvider: RemoteConfigProvider
) : PremiumEntitlementRepository {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _purchaseState = MutableStateFlow<PremiumPurchaseState>(PremiumPurchaseState.Idle)
    override val purchaseState: StateFlow<PremiumPurchaseState> = _purchaseState.asStateFlow()

    private var premiumProductDetails: ProductDetails? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> handlePurchases(purchases.orEmpty(), restored = false)
            BillingClient.BillingResponseCode.USER_CANCELED -> _purchaseState.value = PremiumPurchaseState.Canceled
            else -> {
                val reason = billingResult.debugMessage
                _purchaseState.value = PremiumPurchaseState.Failed(reason)
                crashReporter.recordNonFatal(IllegalStateException("Premium purchase failed: $reason"))
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    override fun refreshPurchases() {
        if (!remoteConfigProvider.premiumEnabled) return

        ensureConnected {
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    handlePurchases(purchases, restored = true)
                } else {
                    val reason = billingResult.debugMessage
                    _purchaseState.value = PremiumPurchaseState.Failed(reason)
                    crashReporter.recordNonFatal(IllegalStateException("Premium restore failed: $reason"))
                }
            }
        }
    }

    override fun launchPremiumPurchase(activity: Activity) {
        if (!remoteConfigProvider.premiumEnabled) {
            _purchaseState.value = PremiumPurchaseState.Failed("Premium disabled")
            return
        }

        _purchaseState.value = PremiumPurchaseState.Loading
        analyticsTracker.logPremiumPurchaseStarted()

        ensureConnected {
            queryPremiumProductDetails { productDetails ->
                if (productDetails == null) {
                    _purchaseState.value = PremiumPurchaseState.Failed("Premium product not configured")
                    return@queryPremiumProductDetails
                }

                val billingParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                        )
                    )
                    .build()

                val result = billingClient.launchBillingFlow(activity, billingParams)
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    _purchaseState.value = PremiumPurchaseState.Failed(result.debugMessage)
                }
            }
        }
    }

    private fun queryPremiumProductDetails(onResult: (ProductDetails?) -> Unit) {
        premiumProductDetails?.let {
            onResult(it)
            return
        }

        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PREMIUM_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(product))
                .build()
        ) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                premiumProductDetails = productDetailsResult.productDetailsList.firstOrNull()
                onResult(premiumProductDetails)
            } else {
                _purchaseState.value = PremiumPurchaseState.Failed(billingResult.debugMessage)
                onResult(null)
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>, restored: Boolean) {
        val premiumPurchase = purchases.firstOrNull { purchase ->
            purchase.products.contains(PREMIUM_PRODUCT_ID)
        }

        if (premiumPurchase == null) {
            _isPremium.value = false
            _purchaseState.value = PremiumPurchaseState.Idle
            return
        }

        when (premiumPurchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                _isPremium.value = true
                acknowledgeIfNeeded(premiumPurchase)
                _purchaseState.value = if (restored) PremiumPurchaseState.Restored else PremiumPurchaseState.Purchased
                if (restored) {
                    analyticsTracker.logPremiumPurchaseRestored()
                } else {
                    analyticsTracker.logPremiumPurchaseCompleted()
                }
            }

            Purchase.PurchaseState.PENDING -> {
                _isPremium.value = false
                _purchaseState.value = PremiumPurchaseState.Pending
            }

            else -> {
                _isPremium.value = false
                _purchaseState.value = PremiumPurchaseState.Idle
            }
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                crashReporter.recordNonFatal(
                    IllegalStateException("Premium acknowledge failed: ${billingResult.debugMessage}")
                )
            }
        }
    }

    private fun ensureConnected(onConnected: () -> Unit) {
        if (billingClient.isReady) {
            onConnected()
            return
        }

        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        onConnected()
                    } else {
                        _purchaseState.value = PremiumPurchaseState.Failed(billingResult.debugMessage)
                    }
                }

                override fun onBillingServiceDisconnected() = Unit
            }
        )
    }

    companion object {
        const val PREMIUM_PRODUCT_ID = "premium_supporter"
    }
}
