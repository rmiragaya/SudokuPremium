package ropa.miragaya.sudokupremium.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

private val HomeBodyFont = FontFamily.SansSerif

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

        if (hasSavedGame) {
            ContinueGamePanel(
                onContinueClick = onContinueClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        PrimaryHomeButton(
            text = "Nueva partida",
            icon = Icons.Default.Add,
            onClick = onNewGameClick,
            isPrimary = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HomeLogo(
    modifier: Modifier = Modifier
) {
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
            text = "Sudoku Premium",
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
private fun ContinueGamePanel(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onContinueClick),
        shape = RoundedCornerShape(20.dp),
        color = SudokuPalette.HomePanel,
        border = BorderStroke(1.dp, SudokuPalette.CellHintBorder.copy(alpha = 0.34f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = HomeBodyFont,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Volvé al tablero en curso",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = HomeBodyFont,
                    color = SudokuPalette.TextSecondary
                )
            }

            Button(
                onClick = onContinueClick,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SudokuPalette.TextAccent)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = SudokuPalette.ScreenBackground,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryHomeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)

    if (isPrimary) {
        Button(
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = modifier
                .height(64.dp)
                .background(SudokuPalette.ButtonGradient, shape)
        ) {
            HomeButtonContent(text = text, icon = icon)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = shape,
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SudokuPalette.ButtonContainer,
                contentColor = SudokuPalette.TextPrimary
            ),
            modifier = modifier.height(58.dp)
        ) {
            HomeButtonContent(text = text, icon = icon)
        }
    }
}

@Composable
private fun HomeButtonContent(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = HomeBodyFont,
            fontWeight = FontWeight.Bold
        )
    }
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
