package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun HomeScreen(
    onNewGameClick: (Difficulty) -> Unit,
    onContinueClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val hasSavedGame by viewModel.hasSavedGame.collectAsStateWithLifecycle()

    var showDifficultySheet by remember { mutableStateOf(false) }

    if (showDifficultySheet) {
        DifficultySelectionSheet(
            onDismiss = { showDifficultySheet = false },
            onDifficultySelected = { difficulty ->
                showDifficultySheet = false
                onNewGameClick(difficulty)
            }
        )
    }

    HomeScreenContent(
        hasSavedGame = hasSavedGame,
        onNewGameClick = { showDifficultySheet = true },
        onContinueClick = onContinueClick
    )
}

@Composable
fun HomeScreenContent(
    hasSavedGame: Boolean,
    onNewGameClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sudoku Premium",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = SudokuPalette.TextPrimary
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (hasSavedGame) {
            MenuButton(
                text = "Continuar Partida",
                onClick = onContinueClick,
                isPrimary = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        MenuButton(
            text = "Nueva Partida",
            onClick = onNewGameClick,
            isPrimary = !hasSavedGame
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {

    val buttonShape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color.Transparent else SudokuPalette.ButtonContainer,
            contentColor = if (isPrimary) Color.White else SudokuPalette.TextPrimary
        ),
        modifier = Modifier
            .width(200.dp)
            .height(50.dp)
            .background(
                brush = if (isPrimary) SudokuPalette.ButtonGradient else SolidColor(Color.Transparent),
                shape = buttonShape
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}



@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(hasSavedGame = true, onNewGameClick = {}, onContinueClick = {})
}