package ropa.miragaya.sudokupremium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ropa.miragaya.sudokupremium.ui.game.GameScreen
import ropa.miragaya.sudokupremium.ui.home.HomeScreen
import ropa.miragaya.sudokupremium.ui.navigation.GameRoute
import ropa.miragaya.sudokupremium.ui.navigation.HomeRoute
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
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<HomeRoute>(
                            enterTransition = {
                                fadeIn(animationSpec = tween(320)) +
                                    scaleIn(
                                        animationSpec = tween(320),
                                        initialScale = 0.94f
                                    )
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(260)) +
                                    scaleOut(
                                        animationSpec = tween(260),
                                        targetScale = 0.94f
                                    )
                            },
                            popEnterTransition = {
                                fadeIn(animationSpec = tween(320)) +
                                    slideInVertically(
                                        animationSpec = tween(320),
                                        initialOffsetY = { -it / 5 }
                                    ) +
                                    scaleIn(
                                        animationSpec = tween(320),
                                        initialScale = 0.96f
                                    )
                            }
                        ) {
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

                        composable<GameRoute>(
                            enterTransition = {
                                fadeIn(animationSpec = tween(520)) +
                                    slideInVertically(
                                        animationSpec = tween(520),
                                        initialOffsetY = { it / 2 }
                                    ) +
                                    scaleIn(
                                        animationSpec = tween(520),
                                        initialScale = 0.86f
                                    )
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(260))
                            },
                            popExitTransition = {
                                fadeOut(animationSpec = tween(320)) +
                                    slideOutVertically(
                                        animationSpec = tween(320),
                                        targetOffsetY = { it / 4 }
                                    ) +
                                    scaleOut(
                                        animationSpec = tween(320),
                                        targetScale = 0.94f
                                    )
                            }
                        ) {
                            GameScreen(
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SudokuPremiumTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            GameScreen(Modifier.padding(innerPadding)) {}
        }
    }
}
