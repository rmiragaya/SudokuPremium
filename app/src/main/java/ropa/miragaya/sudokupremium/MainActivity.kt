package ropa.miragaya.sudokupremium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import ropa.miragaya.sudokupremium.ui.game.GameScreen
import ropa.miragaya.sudokupremium.ui.home.HomeScreen
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import ropa.miragaya.sudokupremium.ui.navigation.HomeRoute
import ropa.miragaya.sudokupremium.ui.navigation.TechniqueDetailRoute
import ropa.miragaya.sudokupremium.ui.navigation.TechniquesRoute
import ropa.miragaya.sudokupremium.ui.techniques.TechniqueDetailScreen
import ropa.miragaya.sudokupremium.ui.techniques.TechniquesScreen
import ropa.miragaya.sudokupremium.ui.theme.SudokuPremiumTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuPremiumTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = { calmScreenEnterTransition() },
                        exitTransition = { calmScreenExitTransition() },
                        popEnterTransition = { calmScreenEnterTransition() },
                        popExitTransition = { calmScreenExitTransition() }
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                onNewGameClick = { difficulty ->
                                    navController.navigate(
                                        GameRoute(createNew = true, difficulty = difficulty)
                                    )
                                },
                                onContinueClick = {
                                    navController.navigate(GameRoute(false))
                                }
                            )
                        }

                        composable<GameRoute> {
                            GameScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onOpenTechniquesClick = {
                                    navController.navigate(TechniquesRoute)
                                },
                                onOpenTechniqueClick = { techniqueId ->
                                    navController.navigate(TechniqueDetailRoute(techniqueId = techniqueId))
                                }
                            )
                        }

                        composable<TechniquesRoute> {
                            TechniquesScreen(
                                onTechniqueClick = { techniqueId ->
                                    navController.navigate(TechniqueDetailRoute(techniqueId = techniqueId))
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<TechniqueDetailRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<TechniqueDetailRoute>()

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
private const val SCREEN_TRANSITION_ENTER_SCALE = 0.97f
private const val SCREEN_TRANSITION_EXIT_SCALE = 0.985f

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SudokuPremiumTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            GameScreen(
                modifier = Modifier.padding(innerPadding),
                onBackClick = {},
                onOpenTechniquesClick = {},
                onOpenTechniqueClick = {}
            )
        }
    }
}
