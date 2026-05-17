package ropa.miragaya.sudokupremium.util

import androidx.annotation.StringRes

interface StringProvider {
    fun get(@StringRes resId: Int): String
}
