package ropa.miragaya.sudokupremium.ui.theme

import androidx.compose.ui.graphics.Color

object SudokuPalette {
    // --- FONDOS ---
    // Fondo de PANTALLA: Mucho más oscuro, casi negro azulado.
    // Esto va a hacer que el tablero resalte.
    val ScreenBackground = Color(0xFF12141C)

    // Fondo del TABLERO: Un poco más claro que el fondo, para separarlo.
    val BoardBackground = Color(0xFF1F2235)

    // --- LINEAS ---
    // Más sutiles. Si el fondo es oscuro, la línea no necesita ser negra,
    // puede ser un azul muy oscuro o transparente.
    val GridLine = Color(0xFF2F3245)

    // --- CELDAS ---
    val CellNormal = Color.Transparent

    // SELECCIÓN: El azul eléctrico de la referencia.
    // Le bajamos un poco el alpha para ver el número, pero el color base es fuerte.
    val CellSelected = Color(0xFF3E7BFA).copy(alpha = 0.35f)

    // HIGHLIGHT (Fila/Columna):
    // CRÍTICO: Bajale la opacidad al 5% (0.05f).
    // Ahora lo tenés muy alto y ensucia la visual. Queremos una guía sutil, no pintar la fila.
    val CellHighlight = Color(0xFF3E7BFA).copy(alpha = 0.05f)

    // ERROR: Rojo pero con transparencia para no tapar el número
    val CellErrorBg = Color(0xFFCF6679).copy(alpha = 0.30f)

    // --- TEXTOS ---
    val TextPrimary = Color(0xFFFFFFFF)       // Blanco puro (Pistas)
    val TextSecondary = Color(0xFF8F93A3)     // Gris para notas
    val TextAccent = Color(0xFF64B5F6)        // Azul clarito para tus inputs (Input Usuario)
    val TextError = Color(0xFFFF5252)         // Rojo

    // --- TECLADO ---
    // Botones oscuros para que la letra blanca resalte
    val ButtonContainer = Color(0xFF23263A)
    val ButtonContent = Color(0xFFFFFFFF)

    // Botón borrar (X)
    val ButtonDestructive = Color(0xFF2D1F1F) // Fondo muy sutil rojizo
    val ButtonDestructiveContent = Color(0xFFE57373)
}