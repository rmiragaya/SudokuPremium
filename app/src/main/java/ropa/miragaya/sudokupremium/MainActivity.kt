package ropa.miragaya.sudokupremium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.analytics.TechniqueOpenSource
import ropa.miragaya.sudokupremium.ui.game.GameScreen
import ropa.miragaya.sudokupremium.ui.home.HomeScreen
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import ropa.miragaya.sudokupremium.ui.navigation.HomeRoute
import ropa.miragaya.sudokupremium.ui.navigation.PremiumRoute
import ropa.miragaya.sudokupremium.ui.navigation.SettingsRoute
import ropa.miragaya.sudokupremium.ui.navigation.TechniqueDetailRoute
import ropa.miragaya.sudokupremium.ui.navigation.TechniquesRoute
import ropa.miragaya.sudokupremium.ui.settings.PremiumScreen
import ropa.miragaya.sudokupremium.ui.settings.SettingsScreen
import ropa.miragaya.sudokupremium.ui.techniques.TechniqueDetailScreen
import ropa.miragaya.sudokupremium.ui.techniques.TechniquesScreen
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.ui.theme.SudokuPremiumTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(SYSTEM_BAR_BACKGROUND),
            navigationBarStyle = SystemBarStyle.dark(SYSTEM_BAR_BACKGROUND)
        )
        setContent {
            SudokuPremiumTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val routeKey = currentBackStackEntry?.destination?.route
                val transitionBlur = remember { Animatable(0f) }
                var hasAnimatedInitialRoute by remember { mutableStateOf(false) }

                LaunchedEffect(routeKey) {
                    if (routeKey == null) return@LaunchedEffect

                    if (hasAnimatedInitialRoute) {
                        transitionBlur.snapTo(SCREEN_TRANSITION_BLUR_DP)
                        transitionBlur.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = SCREEN_TRANSITION_BLUR_MILLIS)
                        )
                    } else {
                        hasAnimatedInitialRoute = true
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SudokuPalette.ScreenBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SudokuPalette.MainGradient)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = HomeRoute,
                            modifier = Modifier
                                .padding(innerPadding)
                                .blur(transitionBlur.value.dp),
                            enterTransition = {
                                if (isInitialDestinationTransition()) {
                                    EnterTransition.None
                                } else {
                                    calmScreenEnterTransition()
                                }
                            },
                            exitTransition = { calmScreenExitTransition() },
                            popEnterTransition = {
                                if (isInitialDestinationTransition()) {
                                    EnterTransition.None
                                } else {
                                    calmScreenEnterTransition()
                                }
                            },
                            popExitTransition = { calmScreenExitTransition() }
                        ) {
                            composable<HomeRoute> {
                                LaunchedEffect(Unit) {
                                    analyticsTracker.logScreenViewed(SCREEN_HOME)
                                }

                                HomeScreen(
                                    onNewGameClick = { difficulty ->
                                        analyticsTracker.logDifficultySelected(difficulty)
                                        navController.navigate(
                                            GameRoute(createNew = true, difficulty = difficulty)
                                        )
                                    },
                                    onContinueClick = {
                                        analyticsTracker.logContinueGameSelected()
                                        navController.navigate(GameRoute(false))
                                    }
                                )
                            }

                            composable<GameRoute> {
                                LaunchedEffect(Unit) {
                                    analyticsTracker.logScreenViewed(SCREEN_GAME)
                                }

                                GameScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onOpenTechniquesClick = {
                                        navController.navigate(TechniquesRoute)
                                    },
                                    onOpenSettingsClick = {
                                        navController.navigate(SettingsRoute)
                                    },
                                    onOpenPremiumClick = {
                                        navController.navigate(PremiumRoute)
                                    },
                                    onOpenTechniqueClick = { techniqueId ->
                                        analyticsTracker.logTechniqueOpened(techniqueId, TechniqueOpenSource.HINT)
                                        navController.navigate(TechniqueDetailRoute(techniqueId = techniqueId))
                                    }
                                )
                            }

                            composable<SettingsRoute> {
                                LaunchedEffect(Unit) {
                                    analyticsTracker.logScreenViewed(SCREEN_SETTINGS)
                                }

                                SettingsScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onOpenPremiumClick = {
                                        navController.navigate(PremiumRoute)
                                    }
                                )
                            }

                            composable<PremiumRoute> {
                                LaunchedEffect(Unit) {
                                    analyticsTracker.logScreenViewed(SCREEN_PREMIUM)
                                }

                                PremiumScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable<TechniquesRoute> {
                                LaunchedEffect(Unit) {
                                    analyticsTracker.logScreenViewed(SCREEN_TECHNIQUES)
                                }

                                TechniquesScreen(
                                    onTechniqueClick = { techniqueId ->
                                        analyticsTracker.logTechniqueOpened(techniqueId, TechniqueOpenSource.LIBRARY)
                                        navController.navigate(TechniqueDetailRoute(techniqueId = techniqueId))
                                    },
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable<TechniqueDetailRoute> { backStackEntry ->
                                val route = backStackEntry.toRoute<TechniqueDetailRoute>()

                                LaunchedEffect(route.techniqueId) {
                                    analyticsTracker.logScreenViewed("$SCREEN_TECHNIQUE_DETAIL:${route.techniqueId}")
                                }

                                TechniqueDetailScreen(
                                    techniqueId = route.techniqueId,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isInitialDestinationTransition(): Boolean {
    return initialState.destination.route == targetState.destination.route
}

private fun calmScreenEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = SCREEN_TRANSITION_ENTER_MILLIS,
            delayMillis = SCREEN_TRANSITION_ENTER_DELAY_MILLIS
        )
    ) +
        scaleIn(
            animationSpec = tween(
                durationMillis = SCREEN_TRANSITION_ENTER_MILLIS,
                delayMillis = SCREEN_TRANSITION_ENTER_DELAY_MILLIS
            ),
            initialScale = SCREEN_TRANSITION_ENTER_SCALE
        )
}

private fun calmScreenExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(durationMillis = SCREEN_TRANSITION_EXIT_MILLIS)) +
        scaleOut(
            animationSpec = tween(durationMillis = SCREEN_TRANSITION_EXIT_MILLIS),
            targetScale = SCREEN_TRANSITION_EXIT_SCALE
        )
}

private const val SCREEN_TRANSITION_ENTER_MILLIS = 860
private const val SCREEN_TRANSITION_ENTER_DELAY_MILLIS = 300
private const val SCREEN_TRANSITION_EXIT_MILLIS = 860
private const val SCREEN_TRANSITION_BLUR_MILLIS = 720
private const val SCREEN_TRANSITION_BLUR_DP = 18f
private const val SCREEN_TRANSITION_ENTER_SCALE = 0.97f
private const val SCREEN_TRANSITION_EXIT_SCALE = 0.985f
private const val SYSTEM_BAR_BACKGROUND = 0xFF010413.toInt()
private const val SCREEN_HOME = "home"
private const val SCREEN_GAME = "game"
private const val SCREEN_SETTINGS = "settings"
private const val SCREEN_PREMIUM = "premium"
private const val SCREEN_TECHNIQUES = "techniques"
private const val SCREEN_TECHNIQUE_DETAIL = "technique_detail"

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SudokuPremiumTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            GameScreen(
                modifier = Modifier.padding(innerPadding),
                onBackClick = {},
                onOpenTechniquesClick = {},
                onOpenSettingsClick = {},
                onOpenPremiumClick = {},
                onOpenTechniqueClick = {}
            )
        }
    }
}
