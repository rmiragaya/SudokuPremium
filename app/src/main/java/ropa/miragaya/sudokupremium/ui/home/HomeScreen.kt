package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun HomeScreen(
    onNewGameClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val hasSavedGame by viewModel.hasSavedGame.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SudokuPalette.ScreenBackground),
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
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) SudokuPalette.TextAccent else SudokuPalette.ButtonContainer,
            contentColor = if (isPrimary) SudokuPalette.ScreenBackground else SudokuPalette.TextPrimary
        ),
        modifier = Modifier
            .width(200.dp)
            .height(50.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}