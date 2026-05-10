package ropa.miragaya.sudokupremium.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.crash.CrashReporter

@Singleton
class FirebaseAuthSessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter
) : AuthSessionManager {

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun ensureAnonymousSession() {
        firebaseAuth.currentUser?.uid?.let { uid ->
            setUserIdentity(uid)
            return
        }

        firebaseAuth.signInAnonymously()
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    setUserIdentity(uid)
                } else {
                    crashReporter.recordNonFatal(IllegalStateException("Anonymous auth succeeded without uid"))
                }
            }
            .addOnFailureListener { throwable ->
                crashReporter.recordNonFatal(throwable)
            }
    }

    private fun setUserIdentity(uid: String) {
        analyticsTracker.setUserId(uid)
        crashReporter.setUserId(uid)
        crashReporter.log("Firebase Auth session ready")
    }
}
