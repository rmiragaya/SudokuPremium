package ropa.miragaya.sudokupremium.settings

data class AppSettings(
    val hapticsEnabled: Boolean = true,
    val hasStartedAnyGame: Boolean = false,
    val hasSeenHowToPlayTutorial: Boolean = false
)
