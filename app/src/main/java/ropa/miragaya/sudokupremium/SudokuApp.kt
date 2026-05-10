package ropa.miragaya.sudokupremium

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import ropa.miragaya.sudokupremium.auth.AuthSessionManager
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider

@HiltAndroidApp
class SudokuApp : Application() {

    @Inject
    lateinit var remoteConfigProvider: RemoteConfigProvider

    @Inject
    lateinit var authSessionManager: AuthSessionManager

    override fun onCreate() {
        super.onCreate()
        remoteConfigProvider.initialize()
        remoteConfigProvider.fetchAndActivate()
        authSessionManager.ensureAnonymousSession()
    }
}
