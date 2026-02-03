package ropa.miragaya.sudokupremium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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