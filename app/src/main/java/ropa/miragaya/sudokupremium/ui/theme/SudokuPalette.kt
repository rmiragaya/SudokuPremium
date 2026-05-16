package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SudokuPalette {
    val Night = Color(0xFF010413)
    val NightSoft = Color(0xFF050A1D)
    val NightPanel = Color(0xFF080D22)
    val WarmWhite = Color(0xFFF4F0E7)
    val MentorCyan = Color(0xFF50D8FF)
    val MentorBlue = Color(0xFF005DFF)
    val MentorIndigo = Color(0xFF2834F4)
    val MentorPurple = Color(0xFF9B2CFF)
    val MentorGold = Color(0xFFF2C84B)
    val BadgeText = Color(0xFF9FE8FF)

    val ScreenBackground = Night

    val BoardBackground = Color(0xFF05091A)

    val GridLine = Color(0xFF15346D).copy(alpha = 0.72f)

    val CellNormal = Color.Transparent

    val CellSelected = MentorIndigo.copy(alpha = 0.28f)

    val CellHighlight = MentorBlue.copy(alpha = 0.15f)

    val CellErrorBg = Color(0xFFFF3D71).copy(alpha = 0.24f)

    val TextPrimary = WarmWhite
    val TextSecondary = Color(0xFFA7AEC5)
    val TextAccent = MentorCyan
    val TextOnAccent = Color(0xFFFFFFFF)
    val TextError = Color(0xFFFF5C8A)

    val ButtonContainer = Color(0xFF060B1F)
    val ButtonContent = Color(0xFFFFFFFF)
    val HomePanel = NightPanel
    val HomeBorder = Color(0xFF25448C).copy(alpha = 0.72f)
    val HomeBadgeBackground = Color(0xFF04081A)

    val ButtonDestructive = Color(0xFF21101B)
    val ButtonDestructiveContent = Color(0xFFFF6E93)

    val CellHint = MentorGold.copy(alpha = 0.20f)
    val CellHintBorder = MentorGold
    val CellEliminationBg = TextError.copy(alpha = 0.14f)
    val CellEliminationBorder = TextError.copy(alpha = 0.75f)

    val MainGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF03081A),
            Night,
            Color(0xFF00020C)
        )
    )

    val PrimaryButtonGradient = Brush.horizontalGradient(
        colors = listOf(
            MentorBlue,
            MentorIndigo,
            Color(0xFF6D12DA)
        )
    )
    val PrimaryButtonSolid = MentorBlue
    val PrimaryButtonBorder = Color.White.copy(alpha = 0.22f)
    val PrimaryButtonSecondaryContainer = Color(0xFF020613).copy(alpha = 0.24f)
    val PrimaryButtonSecondaryBorder = WarmWhite.copy(alpha = 0.56f)
    val PrimaryButtonSecondaryContent = WarmWhite

    val ButtonGradient = PrimaryButtonGradient
}
