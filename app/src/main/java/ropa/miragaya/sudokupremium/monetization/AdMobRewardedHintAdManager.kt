package ropa.miragaya.sudokupremium.monetization

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
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
        if (!BuildConfig.DEBUG && (!remoteConfigProvider.adsEnabled || !remoteConfigProvider.rewardedHintsEnabled)) {
            onResult(RewardedHintAdResult.Failed("Ads disabled"))
            return
        }

        val adUnitId = getAdUnitId()
        if (adUnitId.isBlank()) {
            onResult(RewardedHintAdResult.Failed("Rewarded ad unit is not configured"))
            return
        }

        ensureConsent(activity) {
            MobileAds.initialize(context)
            RewardedAd.load(
                context,
                adUnitId,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        showLoadedAd(activity, ad, onResult)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        val reason = error.message
                        crashReporter.recordNonFatal(IllegalStateException("Rewarded ad failed to load: $reason"))
                        onResult(RewardedHintAdResult.Failed(reason))
                    }
                }
            )
        }
    }

    private fun showLoadedAd(activity: Activity, ad: RewardedAd, onResult: (RewardedHintAdResult) -> Unit) {
        var rewardEarned = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                if (!rewardEarned) {
                    onResult(RewardedHintAdResult.Dismissed)
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                onResult(RewardedHintAdResult.Failed(error.message))
            }
        }

        ad.show(activity) {
            rewardEarned = true
            onResult(RewardedHintAdResult.Earned)
        }
    }

    private fun ensureConsent(activity: Activity, onReady: () -> Unit) {
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

                    if (consentInformation.canRequestAds()) {
                        onReady()
                    }
                }
            },
            { requestError ->
                crashReporter.recordNonFatal(IllegalStateException("UMP request failed: ${requestError.message}"))
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

    private companion object {
        const val DEBUG_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }
}
