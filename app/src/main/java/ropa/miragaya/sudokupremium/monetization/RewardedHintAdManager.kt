package ropa.miragaya.sudokupremium.monetization

import android.app.Activity

interface RewardedHintAdManager {
    fun showRewardedHintAd(activity: Activity, onResult: (RewardedHintAdResult) -> Unit)
}

sealed interface RewardedHintAdResult {
    data object Earned : RewardedHintAdResult
    data object Dismissed : RewardedHintAdResult
    data class Failed(val reason: String?) : RewardedHintAdResult
}
