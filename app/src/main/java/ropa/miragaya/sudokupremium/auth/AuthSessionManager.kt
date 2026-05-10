package ropa.miragaya.sudokupremium.auth

interface AuthSessionManager {
    fun ensureAnonymousSession()

    fun currentUserId(): String?
}
