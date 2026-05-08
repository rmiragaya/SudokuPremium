package ropa.miragaya.sudokupremium.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT

    /**
     * 🗺️ ROADMAP DE ESTRATEGIAS Y DIFICULTADES
     * -----------------------------------------------------------------------
     * El Generador usa estas estrategias para validar si un puzzle es apto
     * para cierta dificultad. Si se requiere una estrategia de nivel HARD
     * para resolverlo, el puzzle se etiqueta como HARD, y asi.
     * -----------------------------------------------------------------------
     *
     * 🟢 EASY
     * - Naked Single Strategy: El número es el único candidato en la celda.
     * - Hidden Single Strategy: El número es el único candidato en la fila/col/caja.
     *
     * 🟡 MEDIUM
     * - Intersection Removal (Pointing/Claiming): El número está restringido a una línea dentro de una caja.
     * - Naked Pair Strategy: Dos celdas tienen exactamente los mismos 2 candidatos (ej: [3,7] y [3,7]).
     * - Hidden Pair Strategy: Dos números aparecen SOLO en dos celdas de un grupo.
     *
     * 🟠 HARD
     * - Naked Triple / Hidden Triple: Igual que los pares, pero con 3 celdas y 3 números.
     * -> Estado: PENDIENTE DE IMPLEMENTAR
     *
     * - X-Wing Strategy: Técnica geométrica. Un número forma un rectángulo en filas/columnas.
     * -> Estado: PENDIENTE (Vital para separar Medium de Hard real).
     *
     * 🔴 EXPERT
     * - Y-Wing (XY-Wing): Patrón de "pivote" y "alas" (ej: AB -> BC -> AC).
     * -> Estado: PENDIENTE
     *
     * - Swordfish: Como el X-Wing pero en una grilla de 3x3 filas/columnas.
     * -> Estado: PENDIENTE (Opcional, es muy rara, pero da prestigio).
     *
     * - Simple Coloring / Chains: (Opcional) Cadenas de lógica.
     */
}
