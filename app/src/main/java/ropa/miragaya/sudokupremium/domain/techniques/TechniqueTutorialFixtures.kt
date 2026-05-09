package ropa.miragaya.sudokupremium.domain.techniques

import ropa.miragaya.sudokupremium.domain.model.Board

data class TechniqueTutorialExample(
    val techniqueId: String,
    val strategyName: String,
    val title: String,
    val description: String,
    val grid: String,
    val notesSpec: String,
    val highlightCells: List<Int>,
    val highlightBoxes: List<Int> = emptyList(),
    val removedNotesSpec: String = ""
) {
    val values: List<Int?> = grid.map { char ->
        val value = char.digitToInt()
        if (value == 0) null else value
    }
    val notes: Map<Int, Set<Int>> = parseNotesSpec(notesSpec)
    val removedNotes: Map<Int, Set<Int>> = parseNotesSpec(removedNotesSpec)
    val eliminationCells: List<Int> = removedNotes.keys.sorted()

    fun toBoard(): Board {
        val cells = Board.fromGridString(grid).cells.map { cell ->
            if (cell.value == null) {
                cell.copy(notes = notes[cell.id].orEmpty())
            } else {
                cell
            }
        }
        return Board(cells)
    }
}

object TechniqueTutorialFixtures {
    val all: List<TechniqueTutorialExample> = listOf(
        nakedSingleExample,
        hiddenSingleExample,
        nakedPairExample,
        intersectionPointingExample,
        intersectionBoxLineExample,
        hiddenPairExample,
        hiddenPairBoxExample,
        nakedTripleBoxExample,
        nakedTripleRowExample,
        xWingRowsExample,
        xWingColumnsExample,
        yWingBoxExample,
        yWingLineExample
    )

    fun forTechnique(techniqueId: String): List<TechniqueTutorialExample> {
        return all.filter { it.techniqueId == techniqueId }
    }
}

private val nakedSingleExample = TechniqueTutorialExample(
    techniqueId = "naked_single",
    strategyName = "Naked Single",
    title = "Ejemplo real",
    description = "El tablero ya deja una sola opción en la casilla amarilla: el 9.",
    grid = "830200940290004003514003862000420000003070420042000009079100280060002094025000010",
    notesSpec = """
        2:67
        4:156
        5:1567
        8:157
        11:67
        12:5678
        13:1568
        15:157
        16:57
        21:79
        22:9
        27:1679
        28:58
        29:1678
        32:15689
        33:13567
        34:357
        35:15678
        36:169
        37:58
        39:5689
        41:15689
        44:1568
        45:167
        48:3568
        49:13568
        50:1568
        51:13567
        52:357
        54:34
        58:3456
        59:56
        62:56
        63:13
        65:18
        66:3578
        67:358
        69:357
        72:34
        75:36789
        76:34689
        77:6789
        78:367
        80:67
    """.trimIndent(),
    highlightCells = listOf(22)
)

private val hiddenSingleExample = TechniqueTutorialExample(
    techniqueId = "hidden_single",
    strategyName = "Hidden Single",
    title = "Ejemplo real",
    description = "En la primera fila, el 3 solo aparece como candidato en la casilla amarilla.",
    grid = "800200000290004003010003860000420000003070400000000009079100080060002004005000010",
    notesSpec = """
        1:345
        2:467
        4:1569
        5:15679
        6:1579
        7:4579
        8:157
        11:67
        12:5678
        13:1568
        15:157
        16:57
        18:457
        20:47
        21:579
        22:59
        26:257
        27:15679
        28:58
        29:1678
        32:15689
        33:13567
        34:357
        35:15678
        36:1569
        37:258
        39:5689
        41:15689
        43:25
        44:12568
        45:14567
        46:2458
        47:124678
        48:3568
        49:13568
        50:1568
        51:123567
        52:2357
        54:34
        58:3456
        59:56
        60:2356
        62:256
        63:13
        65:18
        66:35789
        67:3589
        69:3579
        70:3579
        72:34
        73:2348
        75:36789
        76:34689
        77:6789
        78:23679
        80:267
    """.trimIndent(),
    highlightCells = listOf(1)
)

private val nakedPairExample = TechniqueTutorialExample(
    techniqueId = "naked_pair",
    strategyName = "Naked Pair",
    title = "Ejemplo real",
    description = "Las casillas amarillas reservan 1 y 2. Esos candidatos salen de las casillas rojas.",
    grid = "709050003000032709023090050015900430030040080070300100047000308090003564308400070",
    notesSpec = """
        1:68
        3:168
        5:1468
        6:268
        7:124
        9:14568
        10:568
        11:146
        12:168
        16:14
        18:1468
        21:1678
        23:14678
        24:68
        26:16
        27:268
        31:2678
        32:678
        35:267
        36:269
        38:26
        39:12567
        41:1567
        42:269
        44:2567
        45:24689
        47:246
        49:268
        50:568
        52:29
        53:256
        54:1256
        57:1256
        58:126
        59:1569
        61:129
        63:12
        65:12
        66:1278
        67:1278
        73:56
        76:126
        77:1569
        78:29
        80:12
    """.trimIndent(),
    highlightCells = listOf(63, 65),
    removedNotesSpec = """
        66:12
        67:12
    """.trimIndent()
)

private val intersectionPointingExample = TechniqueTutorialExample(
    techniqueId = "intersection_removal",
    strategyName = "Intersection Removal",
    title = "Pointing dentro de una caja",
    description = "En la caja marcada, el 5 queda alineado en la misma fila y se elimina fuera de la caja.",
    grid = "830200940290804003514793862001420030003070420042008009379146285168002794425987316",
    notesSpec = """
        2:67
        4:156
        5:15
        8:17
        11:67
        13:156
        15:15
        16:57
        27:679
        28:58
        32:59
        33:56
        35:78
        36:69
        37:58
        39:56
        41:159
        44:18
        45:67
        48:356
        49:1356
        51:156
        52:57
        66:35
        67:35
    """.trimIndent(),
    highlightCells = emptyList(),
    highlightBoxes = listOf(2),
    removedNotesSpec = "13:5"
)

private val intersectionBoxLineExample = TechniqueTutorialExample(
    techniqueId = "intersection_removal",
    strategyName = "Intersection Removal",
    title = "Reducción caja-línea",
    description = "En la columna, el 6 solo cae dentro de la caja marcada, así que se borra del resto de esa caja.",
    grid = "830200940290804003514793862001420030003070420042008009379146285168002794425987316",
    notesSpec = """
        2:67
        4:156
        5:15
        8:17
        11:67
        13:16
        15:15
        16:57
        27:679
        28:58
        32:59
        33:56
        35:78
        36:69
        37:58
        39:56
        41:159
        44:18
        45:67
        48:356
        49:1356
        51:156
        52:57
        66:35
        67:35
    """.trimIndent(),
    highlightCells = emptyList(),
    highlightBoxes = listOf(1),
    removedNotesSpec = "49:6"
)

private val hiddenPairExample = TechniqueTutorialExample(
    techniqueId = "hidden_pair",
    strategyName = "Hidden Pair",
    title = "Par escondido en una fila",
    description = "El 2 y el 5 solo viven en las casillas amarillas; los candidatos extra se quitan ahí.",
    grid = "709050003000032709023090050015900430030040080070300100047000308090003564308400070",
    notesSpec = """
        1:68
        3:168
        5:1468
        6:268
        7:124
        9:1456
        10:568
        11:146
        12:168
        16:14
        18:146
        21:1678
        23:14678
        24:68
        26:16
        27:268
        31:2678
        32:678
        35:267
        36:269
        38:26
        39:12567
        41:1567
        42:269
        44:2567
        45:24689
        47:246
        49:268
        50:568
        52:29
        53:256
        54:56
        57:256
        58:126
        59:569
        61:129
        63:12
        65:12
        66:78
        67:78
        73:56
        76:126
        77:569
        78:29
        80:12
    """.trimIndent(),
    highlightCells = listOf(39, 57),
    removedNotesSpec = """
        39:167
        57:6
    """.trimIndent()
)

private val hiddenPairBoxExample = TechniqueTutorialExample(
    techniqueId = "hidden_pair",
    strategyName = "Hidden Pair",
    title = "Par escondido en una caja",
    description = "El 5 y el 7 quedan reservados para las casillas amarillas de la caja.",
    grid = "709050003000032709023090050015900430030041080070300100047000308090003564308400070",
    notesSpec = """
        1:68
        3:168
        5:468
        6:268
        7:124
        9:1456
        10:568
        11:146
        12:168
        16:14
        18:146
        21:1678
        23:4678
        24:68
        26:16
        27:268
        31:2678
        32:678
        35:267
        36:269
        38:26
        39:2567
        42:269
        44:2567
        45:24689
        47:246
        49:268
        50:568
        52:29
        53:256
        54:56
        57:256
        58:126
        59:569
        61:129
        63:12
        65:12
        66:78
        67:78
        73:56
        76:126
        77:569
        78:29
        80:12
    """.trimIndent(),
    highlightCells = listOf(39, 44),
    removedNotesSpec = """
        39:26
        44:26
    """.trimIndent()
)

private val nakedTripleBoxExample = TechniqueTutorialExample(
    techniqueId = "naked_triple",
    strategyName = "Naked Triple",
    title = "Triple en una caja",
    description = "Las tres casillas amarillas reservan 1, 4 y 5; esos números salen de las rojas.",
    grid = "040906700790508060826703900689350007174680300002170600918237546467895231200461879",
    notesSpec = """
        0:35
        2:135
        4:12
        7:128
        8:238
        11:13
        13:124
        15:14
        17:234
        22:14
        25:15
        26:45
        32:24
        33:14
        34:12
        41:29
        43:259
        44:25
        45:35
        46:35
        50:49
        52:89
        53:48
        73:35
        74:35
    """.trimIndent(),
    highlightCells = listOf(15, 25, 26),
    highlightBoxes = listOf(2),
    removedNotesSpec = """
        7:1
        17:4
    """.trimIndent()
)

private val nakedTripleRowExample = TechniqueTutorialExample(
    techniqueId = "naked_triple",
    strategyName = "Naked Triple",
    title = "Triple en una fila",
    description = "El conjunto 5, 7 y 8 queda encerrado en tres casillas y limpia esa fila.",
    grid = "080002695509060182261000437020001968196200754875649213008004506902006301600300809",
    notesSpec = """
        0:347
        2:347
        3:14
        4:13
        10:34
        12:47
        14:37
        21:589
        22:589
        23:58
        27:34
        29:34
        30:57
        31:57
        40:38
        41:38
        54:37
        55:13
        57:179
        58:1279
        61:27
        64:45
        66:578
        67:578
        70:47
        73:145
        74:47
        76:1257
        77:57
        79:247
    """.trimIndent(),
    highlightCells = listOf(66, 67, 77),
    highlightBoxes = listOf(7),
    removedNotesSpec = """
        57:7
        58:7
        76:57
    """.trimIndent()
)

private val xWingRowsExample = TechniqueTutorialExample(
    techniqueId = "x_wing",
    strategyName = "X-Wing",
    title = "X-Wing por filas",
    description = "El 1 aparece en las mismas dos columnas de dos filas. El 1 rojo queda descartado.",
    grid = "830200940290804003514793862001420030003070420042008009379146285168002794425987316",
    notesSpec = """
        2:67
        4:156
        5:15
        8:17
        11:67
        13:16
        15:15
        16:57
        27:679
        28:58
        32:59
        33:56
        35:78
        36:69
        37:58
        39:56
        41:159
        44:18
        45:67
        48:356
        49:135
        51:156
        52:57
        66:35
        67:35
    """.trimIndent(),
    highlightCells = listOf(13, 15, 49, 51),
    removedNotesSpec = "4:1"
)

private val xWingColumnsExample = TechniqueTutorialExample(
    techniqueId = "x_wing",
    strategyName = "X-Wing",
    title = "X-Wing por columnas",
    description = "El 7 aparece en las mismas dos filas de dos columnas. Las marcas rojas pierden el 7.",
    grid = "907305146015407800430900257849000005573804021000500408000600504050040000704050080",
    notesSpec = """
        1:28
        4:28
        9:26
        13:26
        16:39
        17:39
        20:68
        22:168
        23:168
        30:127
        31:1367
        32:1236
        33:367
        34:367
        40:69
        42:69
        45:126
        46:26
        47:126
        49:379
        50:39
        52:379
        54:123
        55:289
        56:128
        58:3789
        59:2389
        61:1379
        63:1236
        65:1268
        66:27
        68:2389
        69:3679
        70:13679
        71:239
        73:269
        75:12
        77:1239
        78:369
        80:239
    """.trimIndent(),
    highlightCells = listOf(49, 52, 58, 61),
    removedNotesSpec = """
        31:7
        34:7
        70:7
    """.trimIndent()
)

private val yWingBoxExample = TechniqueTutorialExample(
    techniqueId = "y_wing",
    strategyName = "Y-Wing",
    title = "Y-Wing con pivote y dos alas",
    description = "Las casillas amarillas fuerzan un 5 en una de las alas; la casilla roja ve ambas alas.",
    grid = "830200940290804003514793862001420030003070420042008009379146285168002794425987316",
    notesSpec = """
        2:67
        4:56
        5:15
        8:17
        11:67
        13:16
        15:15
        16:57
        27:679
        28:58
        32:59
        33:56
        35:78
        36:69
        37:58
        39:56
        41:159
        44:18
        45:67
        48:356
        49:135
        51:156
        52:57
        66:35
        67:35
    """.trimIndent(),
    highlightCells = listOf(35, 52, 28),
    removedNotesSpec = "33:5"
)

private val yWingLineExample = TechniqueTutorialExample(
    techniqueId = "y_wing",
    strategyName = "Y-Wing",
    title = "Y-Wing con eliminación doble",
    description = "Ambas casillas rojas ven las alas del patrón, así que ninguna puede conservar el 9.",
    grid = "830200940290804003514793862001420630003070420042008009379146285168002794425987316",
    notesSpec = """
        2:67
        4:56
        5:15
        8:17
        11:67
        13:16
        15:15
        16:57
        27:79
        28:58
        32:59
        35:78
        36:69
        37:58
        39:56
        41:159
        44:18
        45:67
        48:36
        49:13
        51:15
        52:57
        66:35
        67:35
    """.trimIndent(),
    highlightCells = listOf(39, 32, 36),
    removedNotesSpec = """
        27:9
        41:9
    """.trimIndent()
)

private fun parseNotesSpec(spec: String): Map<Int, Set<Int>> {
    return spec
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .associate { line ->
            val parts = line.split(":")
            val cellId = parts[0].toInt()
            val notes = parts[1].map { it.digitToInt() }.toSet()
            cellId to notes
        }
}
