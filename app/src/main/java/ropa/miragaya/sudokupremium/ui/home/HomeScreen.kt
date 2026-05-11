package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

private val HomeBodyFont = FontFamily.SansSerif
private const val DIFFICULTY_SHEET_RESET_AFTER_NAVIGATION_MILLIS = 1_100L

@Composable
fun HomeScreen(
    onNewGameClick: (Difficulty) -> Unit,
    onContinueClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val hasSavedGame by viewModel.hasSavedGame.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showDifficultySheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreenContent(
            hasSavedGame = hasSavedGame,
            onNewGameClick = { showDifficultySheet = true },
            onContinueClick = onContinueClick
        )

        if (showDifficultySheet) {
            DifficultySelectionSheet(
                onDismiss = { showDifficultySheet = false },
                onDifficultySelected = { difficulty ->
                    onNewGameClick(difficulty)
                    scope.launch {
                        delay(DIFFICULTY_SHEET_RESET_AFTER_NAVIGATION_MILLIS)
                        showDifficultySheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreenContent(hasSavedGame: Boolean, onNewGameClick: () -> Unit, onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
            .padding(horizontal = 22.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeHeader()

        Spacer(modifier = Modifier.weight(0.55f))

        HomeLogo(
            modifier = Modifier.size(190.dp)
        )

        Spacer(modifier = Modifier.weight(0.9f))

        HomeActionButton(
            text = "Nueva partida",
            onClick = onNewGameClick,
            isPrimary = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (hasSavedGame) {
            Spacer(modifier = Modifier.height(12.dp))

            HomeActionButton(
                text = "Continuar",
                onClick = onContinueClick,
                isPrimary = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HomeLogo(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(44.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
        shadowElevation = 14.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(3) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { col ->
                            val isAccent = row == 1 && col == 1
                            val isHint = row == 0 && col == 2
                            val isFilled = row == col || row + col == 2

                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isAccent -> SudokuPalette.TextAccent
                                    isHint -> SudokuPalette.CellHintBorder
                                    isFilled -> SudokuPalette.TextAccent.copy(alpha = 0.24f)
                                    else -> SudokuPalette.BoardBackground
                                },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isAccent) {
                                        SudokuPalette.TextAccent
                                    } else if (isHint) {
                                        SudokuPalette.CellHintBorder
                                    } else {
                                        SudokuPalette.HomeBorder
                                    }
                                )
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = SudokuPalette.HomeBadgeBackground,
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = SudokuPalette.TextAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Logic trainer",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = HomeBodyFont,
                    color = SudokuPalette.TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Sudoku Mentor",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = SudokuPalette.TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Pensado para resolver, no adivinar.",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = HomeBodyFont,
            color = SudokuPalette.TextSecondary,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.12
        )
    }
}

@Composable
private fun HomeActionButton(text: String, onClick: () -> Unit, isPrimary: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)

    if (isPrimary) {
        Box(
            modifier = modifier
                .height(64.dp)
                .background(SudokuPalette.ButtonGradient, shape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            HomeButtonText(
                text = text,
                color = Color.White
            )
        }
    } else {
        Surface(
            modifier = modifier
                .height(58.dp)
                .clickable(onClick = onClick),
            shape = shape,
            color = SudokuPalette.HomePanel.copy(alpha = 0.86f),
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                HomeButtonText(
                    text = text,
                    color = SudokuPalette.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun HomeButtonText(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = HomeBodyFont,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12141C
)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(hasSavedGame = true, onNewGameClick = {}, onContinueClick = {})
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12141C
)
@Composable
fun HomeScreenNoSavedGamePreview() {
    HomeScreenContent(hasSavedGame = false, onNewGameClick = {}, onContinueClick = {})
}
