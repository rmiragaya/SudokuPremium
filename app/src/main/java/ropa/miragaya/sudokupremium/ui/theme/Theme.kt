package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SudokuPalette.PrimaryButtonSolid,
    onPrimary = SudokuPalette.TextOnAccent,
    secondary = SudokuPalette.CellHintBorder,
    onSecondary = SudokuPalette.ScreenBackground,
    tertiary = SudokuPalette.CellEliminationBorder,
    background = SudokuPalette.ScreenBackground,
    onBackground = SudokuPalette.TextPrimary,
    surface = SudokuPalette.HomePanel,
    onSurface = SudokuPalette.TextPrimary,
    surfaceVariant = SudokuPalette.ButtonContainer,
    onSurfaceVariant = SudokuPalette.TextSecondary,
    error = SudokuPalette.TextError,
    onError = SudokuPalette.TextPrimary
)

@Composable
fun SudokuPremiumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
