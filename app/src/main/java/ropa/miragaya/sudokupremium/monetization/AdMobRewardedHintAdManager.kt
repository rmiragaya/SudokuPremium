package ropa.miragaya.sudokupremium.monetization

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider
import ropa.miragaya.sudokupremium.crash.CrashReporter

@Singleton
class AdMobRewardedHintAdManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteConfigProvider: RemoteConfigProvider,
    private val crashReporter: CrashReporter
) : RewardedHintAdManager {

    override fun showRewardedHintAd(activity: Activity, onResult: (RewardedHintAdResult) -> Unit) {
        val resultDelivered = AtomicBoolean(false)
        val mainHandler = Handler(Looper.getMainLooper())
        var latestStage = STAGE_STARTED
        var latestCanRequestAds: Boolean? = null

        fun setContext(stage: String, reason: String? = null, canRequestAds: Boolean? = null) {
            if (resultDelivered.get()) {
                crashReporter.log("Ignored rewarded ad context after completion: stage=$stage")
                return
            }

            latestStage = stage
            canRequestAds?.let { latestCanRequestAds = it }
            crashReporter.setRewardedAdContext(
                stage = stage,
                reason = reason,
                adsEnabled = remoteConfigProvider.adsEnabled,
                rewardedHintsEnabled = remoteConfigProvider.rewardedHintsEnabled,
                adUnitConfigured = getAdUnitId().isNotBlank(),
                canRequestAds = latestCanRequestAds
            )
        }

        fun complete(result: RewardedHintAdResult) {
            if (!resultDelivered.compareAndSet(false, true)) {
                crashReporter.log("Ignored duplicate rewarded ad result at stage=$latestStage: $result")
                return
            }

            mainHandler.removeCallbacksAndMessages(null)
            onResult(result)
        }

        fun fail(stage: String, reason: String, canRequestAds: Boolean? = null, recordNonFatal: Boolean = true) {
            if (resultDelivered.get()) {
                crashReporter.log("Ignored late rewarded ad failure at stage=$stage: $reason")
                return
            }

            setContext(stage = stage, reason = reason, canRequestAds = canRequestAds)
            Log.w(TAG, reason)
            if (recordNonFatal) {
                crashReporter.recordNonFatal(IllegalStateException(reason))
            } else {
                crashReporter.log(reason)
            }
            complete(RewardedHintAdResult.Failed(reason))
        }

        val timeoutRunnable = Runnable {
            fail(
                stage = STAGE_TIMEOUT,
                reason = "Rewarded ad timed out at stage=$latestStage",
                recordNonFatal = true
            )
        }
        mainHandler.postDelayed(timeoutRunnable, REWARDED_AD_TIMEOUT_MS)

        setContext(STAGE_STARTED)

        if (!BuildConfig.DEBUG && (!remoteConfigProvider.adsEnabled || !remoteConfigProvider.rewardedHintsEnabled)) {
            fail(
                stage = STAGE_CONFIG_DISABLED,
                reason = "Rewarded ads disabled by Remote Config",
                recordNonFatal = false
            )
            return
        }

        val adUnitId = getAdUnitId()
        if (adUnitId.isBlank()) {
            fail(
                stage = STAGE_CONFIG_MISSING_AD_UNIT,
                reason = "Rewarded ad unit is not configured"
            )
            return
        }

        crashReporter.log(
            "Rewarded ad request started: debug=${BuildConfig.DEBUG}, " +
                "adsEnabled=${remoteConfigProvider.adsEnabled}, " +
                "rewardedHintsEnabled=${remoteConfigProvider.rewardedHintsEnabled}, " +
                "adUnitConfigured=${adUnitId.isNotBlank()}"
        )
        Log.d(TAG, "Rewarded ad request started. debug=${BuildConfig.DEBUG}")

        ensureConsent(
            activity = activity,
            onReady = {
                setContext(STAGE_CONSENT_READY, canRequestAds = true)
                loadRewardedAd(
                    activity = activity,
                    adUnitId = adUnitId,
                    setContext = { stage, reason, canRequestAds -> setContext(stage, reason, canRequestAds) },
                    complete = { result -> complete(result) },
                    fail = { stage, reason, canRequestAds, recordNonFatal ->
                        fail(stage, reason, canRequestAds, recordNonFatal)
                    }
                )
            },
            onFailure = { reason, canRequestAds ->
                fail(
                    stage = STAGE_CONSENT_FAILED,
                    reason = reason,
                    canRequestAds = canRequestAds
                )
            }
        )
    }

    private fun loadRewardedAd(
        activity: Activity,
        adUnitId: String,
        setContext: (String, String?, Boolean?) -> Unit,
        complete: (RewardedHintAdResult) -> Unit,
        fail: (String, String, Boolean?, Boolean) -> Unit
    ) {
        setContext(STAGE_LOADING, null, null)
        crashReporter.log("Rewarded ad loading")

        MobileAds.initialize(context)
        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded")
                    crashReporter.log("Rewarded ad loaded")
                    setContext(STAGE_LOADED, null, null)
                    showLoadedAd(
                        activity = activity,
                        ad = ad,
                        setContext = setContext,
                        complete = complete,
                        fail = fail
                    )
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    val reason = error.toFailureReason(prefix = "Rewarded ad failed to load")
                    fail(STAGE_LOAD_FAILED, reason, null, true)
                }
            }
        )
    }

    private fun showLoadedAd(
        activity: Activity,
        ad: RewardedAd,
        setContext: (String, String?, Boolean?) -> Unit,
        complete: (RewardedHintAdResult) -> Unit,
        fail: (String, String, Boolean?, Boolean) -> Unit
    ) {
        var rewardEarned = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                if (!rewardEarned) {
                    Log.d(TAG, "Rewarded ad dismissed without reward")
                    crashReporter.log("Rewarded ad dismissed without reward")
                    setContext(STAGE_DISMISSED, null, null)
                    complete(RewardedHintAdResult.Dismissed)
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                val reason = error.toFailureReason(
                    prefix = "Rewarded ad failed to show",
                    responseInfo = ad.responseInfo.toDiagnosticString()
                )
                fail(STAGE_SHOW_FAILED, reason, null, true)
            }
        }

        setContext(STAGE_SHOWING, null, null)
        ad.show(activity) {
            rewardEarned = true
            Log.d(TAG, "Rewarded ad reward earned")
            crashReporter.log("Rewarded ad reward earned")
            setContext(STAGE_EARNED, null, null)
            complete(RewardedHintAdResult.Earned)
        }
    }

    private fun ensureConsent(activity: Activity, onReady: () -> Unit, onFailure: (String, Boolean?) -> Unit) {
        if (BuildConfig.DEBUG) {
            onReady()
            return
        }

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        crashReporter.recordNonFatal(IllegalStateException("UMP consent failed: ${formError.message}"))
                    }

                    val canRequestAds = consentInformation.canRequestAds()
                    if (canRequestAds) {
                        onReady()
                    } else {
                        val formErrorMessage = formError?.message ?: "none"
                        onFailure(
                            "UMP consent unavailable: canRequestAds=false, formError=$formErrorMessage",
                            false
                        )
                    }
                }
            },
            { requestError ->
                onFailure("UMP request failed: ${requestError.message}", null)
            }
        )
    }

    private fun getAdUnitId(): String {
        return if (BuildConfig.DEBUG) {
            DEBUG_REWARDED_AD_UNIT_ID
        } else {
            remoteConfigProvider.rewardedHintAdUnitId
        }
    }

    private fun LoadAdError.toFailureReason(prefix: String): String {
        return "$prefix: code=$code, domain=$domain, message=$message, ${responseInfo.toDiagnosticString()}"
    }

    private fun AdError.toFailureReason(prefix: String, responseInfo: String): String {
        return "$prefix: code=$code, domain=$domain, message=$message, $responseInfo"
    }

    private fun ResponseInfo?.toDiagnosticString(): String {
        if (this == null) return "responseInfo=none"

        return "responseId=${responseId ?: VALUE_NONE}, mediationAdapter=${mediationAdapterClassName ?: VALUE_NONE}"
    }

    private companion object {
        const val DEBUG_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TAG = "RewardedHintAds"
        const val REWARDED_AD_TIMEOUT_MS = 30_000L
        const val VALUE_NONE = "none"
        const val STAGE_STARTED = "started"
        const val STAGE_CONFIG_DISABLED = "config_disabled"
        const val STAGE_CONFIG_MISSING_AD_UNIT = "config_missing_ad_unit"
        const val STAGE_CONSENT_READY = "consent_ready"
        const val STAGE_CONSENT_FAILED = "consent_failed"
        const val STAGE_LOADING = "loading"
        const val STAGE_LOADED = "loaded"
        const val STAGE_LOAD_FAILED = "load_failed"
        const val STAGE_SHOWING = "showing"
        const val STAGE_SHOW_FAILED = "show_failed"
        const val STAGE_DISMISSED = "dismissed"
        const val STAGE_EARNED = "earned"
        const val STAGE_TIMEOUT = "timeout"
    }
}
