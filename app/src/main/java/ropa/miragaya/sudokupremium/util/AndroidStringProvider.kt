package ropa.miragaya.sudokupremium.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidStringProvider @Inject constructor(@param:ApplicationContext private val context: Context) :
    StringProvider {

    override fun get(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}
