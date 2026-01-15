package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.ui.graphics.Color

object SudokuPalette {
    // --- FONDOS ---
    // Ese azul/gris muy oscuro del fondo general
    val ScreenBackground = Color(0xFF161823) // #161823

    // El color del tablero (un poco más claro que el fondo para dar profundidad)
    val BoardBackground = Color(0xFF222432)  // #222432

    // --- LINEAS ---
    // Las líneas ahora son oscuras pero sutiles, no negras puras
    val GridLine = Color(0xFF2F3245)

    // --- CELDAS ---
    // Estado normal (transparente para ver el BoardBackground)
    val CellNormal = Color.Transparent

    // Seleccionada: Ese azul eléctrico de la imagen, pero con transparencia
    val CellSelected = Color(0xFF3E7BFA).copy(alpha = 0.25f)

    // Error: Un rojo que combine con el dark mode
    val CellErrorBg = Color(0xFFCF6679).copy(alpha = 0.25f)

    // Celda casi seleccionada
    val CellHighlight = Color(0xFF3E7BFA).copy(alpha = 0.10f)

    // --- TEXTOS ---
    val TextPrimary = Color(0xFFFFFFFF)       // Blanco puro
    val TextSecondary = Color(0xFF8F93A3)     // Gris azulado para notas o teclado
    val TextAccent = Color(0xFF4D8EFF)        // Azul brillante (Input usuario)
    val TextError = Color(0xFFFF5252)         // Rojo alerta

    // --- TECLADO ---
    val ButtonContainer = Color(0xFF2A2D3E)   // Gris botón
    val ButtonContent = Color(0xFFFFFFFF)     // Texto botón
    val ButtonSelected = Color(0xFF4D8EFF)    // Botón apretado (Azul)
    val ButtonDestructive = Color(0xFF3E2A2A) // Fondo rojizo oscuro
    val ButtonDestructiveContent = Color(0xFFFF5252)
}