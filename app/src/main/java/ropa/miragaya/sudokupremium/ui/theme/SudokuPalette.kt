package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.ui.graphics.Color

object SudokuPalette {
    val ScreenBackground = Color(0xFF12141C)

    val BoardBackground = Color(0xFF1F2235)

    val GridLine = Color(0xFF2F3245)

    val CellNormal = Color.Transparent

    val CellSelected = Color(0xFF3E7BFA).copy(alpha = 0.35f)

    val CellHighlight = Color(0xFF3E7BFA).copy(alpha = 0.05f)

    val CellErrorBg = Color(0xFFCF6679).copy(alpha = 0.30f)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFF8F93A3)
    val TextAccent = Color(0xFF64B5F6)
    val TextError = Color(0xFFFF5252)

    val ButtonContainer = Color(0xFF23263A)
    val ButtonContent = Color(0xFFFFFFFF)

    val ButtonDestructive = Color(0xFF2D1F1F)
    val ButtonDestructiveContent = Color(0xFFE57373)
}