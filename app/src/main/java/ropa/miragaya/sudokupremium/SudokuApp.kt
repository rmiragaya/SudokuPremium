package ropa.miragaya.sudokupremium

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider

@HiltAndroidApp
class SudokuApp : Application() {

    @Inject
    lateinit var remoteConfigProvider: RemoteConfigProvider

    override fun onCreate() {
        super.onCreate()
        remoteConfigProvider.initialize()
        remoteConfigProvider.fetchAndActivate()
    }
}
