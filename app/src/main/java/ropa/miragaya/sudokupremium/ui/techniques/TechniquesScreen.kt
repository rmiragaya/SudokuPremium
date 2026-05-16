package ropa.miragaya.sudokupremium.ui.techniques

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.domain.techniques.TechniqueTutorialExample
import ropa.miragaya.sudokupremium.domain.techniques.TechniqueTutorialFixtures
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun TechniquesScreen(onTechniqueClick: (String) -> Unit, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        TechniquesTopBar(onBackClick = onBackClick)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 18.dp,
                top = 12.dp,
                end = 18.dp,
                bottom = 24.dp
            )
        ) {
            item {
                TechniquesIntroCard()
            }

            items(techniques, key = { it.id }) { technique ->
                TechniqueListItem(
                    technique = technique,
                    onClick = { onTechniqueClick(technique.id) }
                )
            }
        }
    }
}

@Composable
fun TechniqueDetailScreen(techniqueId: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val technique = techniques.firstOrNull { it.id == techniqueId } ?: techniques.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        TechniquesTopBar(
            title = technique.title,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TechniqueHeroCard(technique = technique)
            TechniqueDetailCard(technique = technique)
            TechniqueDeepDiveCard(technique = technique)
            technique.examples.forEach { example ->
                TechniqueExampleSection(
                    technique = technique,
                    example = example
                )
            }
            TechniqueSourceCard(technique = technique)
        }
    }
}

@Composable
private fun TechniquesTopBar(onBackClick: () -> Unit, title: String? = null) {
    val topBarTitle = title ?: stringResource(R.string.techniques_title)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 12.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = SudokuPalette.TextSecondary
            )
        }

        Text(
            text = topBarTitle,
            style = MaterialTheme.typography.titleLarge,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun TechniquesIntroCard() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = SudokuPalette.HomeBadgeBackground,
                border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = SudokuPalette.TextAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = stringResource(R.string.techniques_badge),
                        style = MaterialTheme.typography.labelMedium,
                        color = SudokuPalette.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = stringResource(R.string.techniques_intro_title),
                style = MaterialTheme.typography.headlineSmall,
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.techniques_intro_body),
                style = MaterialTheme.typography.bodyLarge,
                color = SudokuPalette.TextSecondary
            )
        }
    }
}

@Composable
private fun TechniqueListItem(technique: TechniqueUiModel, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = technique.accent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, technique.accent.copy(alpha = 0.42f))
            ) {
                Icon(
                    imageVector = technique.icon,
                    contentDescription = null,
                    tint = technique.accent,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(21.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = technique.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = technique.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SudokuPalette.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TechniqueLevelPill(
                text = technique.level,
                accent = technique.accent
            )
        }
    }
}

@Composable
private fun TechniqueHeroCard(technique: TechniqueUiModel) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, technique.accent.copy(alpha = 0.52f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechniqueLevelPill(
                text = technique.level,
                accent = technique.accent
            )

            Text(
                text = technique.title,
                style = MaterialTheme.typography.headlineSmall,
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = technique.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = SudokuPalette.TextSecondary
            )
        }
    }
}

@Composable
private fun TechniqueDetailCard(technique: TechniqueUiModel) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.BoardBackground.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.techniques_idea),
                style = MaterialTheme.typography.titleMedium,
                color = technique.accent,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = technique.idea,
                style = MaterialTheme.typography.bodyLarge,
                color = SudokuPalette.TextPrimary
            )

            TechniqueTextBlock(
                title = stringResource(R.string.techniques_rule),
                body = technique.rule,
                accent = technique.accent
            )

            TechniqueStepsBlock(
                steps = technique.steps,
                accent = technique.accent
            )

            TechniqueTextBlock(
                title = stringResource(R.string.techniques_result),
                body = technique.result,
                accent = technique.accent
            )
        }
    }
}

@Composable
private fun TechniqueDeepDiveCard(technique: TechniqueUiModel) {
    if (!technique.hasDeepDive) return

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.BoardBackground.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, technique.accent.copy(alpha = 0.38f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            technique.whatToWatch?.let { body ->
                TechniqueTextBlock(
                    title = stringResource(R.string.techniques_what_to_watch),
                    body = body,
                    accent = technique.accent
                )
            }

            technique.whyItWorks?.let { body ->
                TechniqueTextBlock(
                    title = stringResource(R.string.techniques_why_it_works),
                    body = body,
                    accent = technique.accent
                )
            }

            TechniqueBulletBlock(
                title = stringResource(R.string.techniques_common_mistakes),
                items = technique.commonMistakes,
                accent = technique.accent
            )

            TechniqueBulletBlock(
                title = stringResource(R.string.techniques_practical_checklist),
                items = technique.practicalChecklist,
                accent = technique.accent
            )
        }
    }
}

@Composable
private fun TechniqueTextBlock(title: String, body: String, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = accent,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = SudokuPalette.TextSecondary
        )
    }
}

@Composable
private fun TechniqueBulletBlock(title: String, items: List<String>, accent: Color) {
    if (items.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = accent,
            fontWeight = FontWeight.Bold
        )

        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyLarge,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TechniqueStepsBlock(steps: List<String>, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.techniques_how_to_recognize),
            style = MaterialTheme.typography.titleSmall,
            color = accent,
            fontWeight = FontWeight.Bold
        )

        steps.forEachIndexed { index, step ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TechniqueExampleSection(technique: TechniqueUiModel, example: TechniqueTutorialExample) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.BoardBackground.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = example.title,
                style = MaterialTheme.typography.titleMedium,
                color = technique.accent,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = example.description,
                style = MaterialTheme.typography.bodyMedium,
                color = SudokuPalette.TextSecondary
            )

            TechniqueExampleBoard(
                technique = technique,
                example = example
            )
        }
    }
}

@Composable
private fun TechniqueExampleBoard(technique: TechniqueUiModel, example: TechniqueTutorialExample) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(SudokuPalette.ScreenBackground)
            .drawTechniqueBoardBoxes()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(TechniqueBoardPadding),
            verticalArrangement = Arrangement.spacedBy(TechniqueCellSpacing)
        ) {
            repeat(9) { row ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(TechniqueCellSpacing)
                ) {
                    repeat(9) { col ->
                        val cellIndex = row * 9 + col
                        val isPatternCell = cellIndex in example.highlightCells
                        val isEliminationCell = cellIndex in example.eliminationCells
                        val isHighlightedBoxCell = cellBoxIndex(row, col) in example.highlightBoxes
                        val value = example.values[cellIndex]
                        val candidates = example.notes[cellIndex].orEmpty()
                        val removedCandidates = example.removedNotes[cellIndex].orEmpty()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(
                                    color = when {
                                        isEliminationCell -> SudokuPalette.CellEliminationBg
                                        isPatternCell -> SudokuPalette.CellHint
                                        isHighlightedBoxCell -> technique.accent.copy(alpha = 0.12f)
                                        else -> SudokuPalette.BoardBackground
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (value != null) {
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 23.sp,
                                        lineHeight = 24.sp
                                    ),
                                    color = if (isPatternCell) technique.accent else SudokuPalette.TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (candidates.isNotEmpty()) {
                                CandidateNotesGrid(
                                    candidates = candidates,
                                    removedCandidates = removedCandidates,
                                    accent = technique.accent,
                                    isPatternCell = isPatternCell
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateNotesGrid(
    candidates: Set<Int>,
    removedCandidates: Set<Int>,
    accent: Color,
    isPatternCell: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) { noteRow ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { noteCol ->
                    val candidate = noteRow * 3 + noteCol + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (candidate in candidates) {
                            Text(
                                text = candidate.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    lineHeight = 9.5.sp
                                ),
                                color = when {
                                    candidate in removedCandidates -> SudokuPalette.TextError
                                    isPatternCell -> accent
                                    else -> SudokuPalette.TextSecondary.copy(alpha = 0.82f)
                                },
                                fontWeight = if (candidate in removedCandidates || isPatternCell) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun cellBoxIndex(row: Int, col: Int): Int {
    return (row / 3) * 3 + (col / 3)
}

private fun Modifier.drawTechniqueBoardBoxes(): Modifier = drawWithContent {
    drawContent()

    val boardPadding = TechniqueBoardPadding.toPx()
    val start = boardPadding
    val end = size.width - boardPadding
    val boardSize = end - start
    val boxSize = boardSize / 3f
    val boxLineColor = SudokuPalette.TextPrimary.copy(alpha = 0.58f)
    val outerLineColor = SudokuPalette.TextAccent.copy(alpha = 0.72f)
    val boxLineWidth = 3.dp.toPx()
    val outerLineWidth = 3.5.dp.toPx()

    repeat(4) { index ->
        val position = start + boxSize * index
        val lineColor = if (index == 0 || index == 3) outerLineColor else boxLineColor
        val lineWidth = if (index == 0 || index == 3) outerLineWidth else boxLineWidth

        drawLine(
            color = lineColor,
            start = Offset(x = position, y = start),
            end = Offset(x = position, y = end),
            strokeWidth = lineWidth
        )

        drawLine(
            color = lineColor,
            start = Offset(x = start, y = position),
            end = Offset(x = end, y = position),
            strokeWidth = lineWidth
        )
    }
}

@Composable
private fun TechniqueSourceCard(technique: TechniqueUiModel) {
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(technique.sourceUrl) },
        shape = RoundedCornerShape(18.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.68f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.techniques_source_title),
                style = MaterialTheme.typography.labelMedium,
                color = technique.accent,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.techniques_source_body),
                style = MaterialTheme.typography.bodyMedium,
                color = SudokuPalette.TextSecondary
            )
        }
    }
}

@Composable
private fun TechniqueLevelPill(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

private data class TechniqueUiModel(
    val id: String,
    val title: String,
    val level: String,
    val summary: String,
    val idea: String,
    val rule: String,
    val steps: List<String>,
    val result: String,
    val whatToWatch: String? = null,
    val whyItWorks: String? = null,
    val commonMistakes: List<String> = emptyList(),
    val practicalChecklist: List<String> = emptyList(),
    val sourceUrl: String,
    val examples: List<TechniqueTutorialExample>,
    val accent: Color,
    val icon: ImageVector
) {
    val hasDeepDive: Boolean =
        whatToWatch != null ||
            whyItWorks != null ||
            commonMistakes.isNotEmpty() ||
            practicalChecklist.isNotEmpty()
}

private val TechniqueBoardPadding: Dp = 6.dp
private val TechniqueCellSpacing: Dp = 1.dp

private val techniques = listOf(
    TechniqueUiModel(
        id = "naked_single",
        title = "Naked Single",
        level = "Easy",
        summary = "Una casilla tiene un único candidato posible.",
        idea = "La técnica aparece cuando una casilla ya quedó reducida a una sola posibilidad. " +
            "No hace falta comparar posiciones: si el candidato es único, ese número va ahí.",
        rule = "Si una casilla vacía tiene un solo candidato legal, se completa con ese número.",
        steps = listOf(
            "Mirá una casilla vacía y revisá qué números siguen permitidos por su fila, columna y caja.",
            "Si todos los números salvo uno quedan descartados, la decisión es directa.",
            "Colocá el número y actualizá candidatos alrededor."
        ),
        result = "Se agrega un número al tablero. Es la pista más directa y suele abrir nuevas oportunidades cerca.",
        sourceUrl = "https://www.sudokuwiki.org/Getting_Started",
        examples = TechniqueTutorialFixtures.forTechnique("naked_single"),
        accent = SudokuPalette.MentorCyan,
        icon = Icons.Default.School
    ),
    TechniqueUiModel(
        id = "hidden_single",
        title = "Hidden Single",
        level = "Easy",
        summary = "Un candidato solo aparece en una casilla dentro de una fila, columna o caja.",
        idea = "El número puede estar mezclado con otros candidatos, pero dentro de una región " +
            "hay una sola casilla donde todavía puede vivir.",
        rule = "Si un candidato aparece una sola vez en una fila, columna o caja, esa casilla debe tomar ese valor.",
        steps = listOf(
            "Elegí una región: una fila, una columna o una caja 3x3.",
            "Buscá un número y contá en qué casillas de esa región aparece como candidato.",
            "Si aparece en una sola casilla, ese número queda resuelto ahí."
        ),
        result = "Se coloca un número aunque la casilla todavía tenga otros candidatos visibles.",
        sourceUrl = "https://www.sudokuwiki.org/Getting_Started",
        examples = TechniqueTutorialFixtures.forTechnique("hidden_single"),
        accent = SudokuPalette.MentorCyan,
        icon = Icons.Default.School
    ),
    TechniqueUiModel(
        id = "naked_pair",
        title = "Naked Pair",
        level = "Medium",
        summary = "Dos casillas comparten exactamente los mismos dos candidatos.",
        idea = "Dos casillas de la misma región forman una reserva: entre ellas van a contener " +
            "esos dos números, en algún orden.",
        rule = "Si dos casillas de una misma región tienen exactamente los mismos dos candidatos, " +
            "esos candidatos no pueden aparecer en otras casillas de esa región.",
        steps = listOf(
            "Buscá dos casillas en una misma fila, columna o caja.",
            "Confirmá que ambas tengan solo los mismos dos candidatos.",
            "Quitá esos dos candidatos del resto de la región."
        ),
        result = "No coloca un número de inmediato, pero limpia candidatos y puede revelar singles después.",
        sourceUrl = "https://www.sudokuwiki.org/Naked_Candidates",
        examples = TechniqueTutorialFixtures.forTechnique("naked_pair"),
        accent = SudokuPalette.TextAccent,
        icon = Icons.Default.Psychology
    ),
    TechniqueUiModel(
        id = "intersection_removal",
        title = "Intersection Removal",
        level = "Medium",
        summary = "Un candidato queda encerrado en la intersección entre una caja y una línea.",
        idea = "Esta técnica mira dos regiones al mismo tiempo: una caja 3x3 y una fila o columna. " +
            "Si dentro de una caja todas las posiciones posibles de un número quedan alineadas, " +
            "ese número queda obligado a salir de esa caja por esa línea.",
        rule = "Cuando un candidato de una caja solo puede estar en una fila o columna, ese candidato " +
            "se elimina del resto de esa misma fila o columna fuera de la caja. También puede ocurrir " +
            "al revés: una fila o columna puede limitar un candidato a una sola caja.",
        steps = listOf(
            "Elegí una caja y un candidato que aparezca dos o tres veces dentro de esa caja.",
            "Revisá si todas esas apariciones caen en una sola fila o en una sola columna.",
            "Como el número debe quedar dentro de la caja, quitá ese candidato del resto de la línea.",
            "Si mirás desde una línea y todas las posiciones caen en la misma caja, limpiá el resto de esa caja."
        ),
        result = "No resuelve la casilla final todavía, pero borra candidatos que parecen posibles " +
            "si se mira una sola región. Es una buena técnica puente entre singles y pares.",
        whatToWatch = "Prestá atención a candidatos que, dentro de una caja, aparecen todos en la misma fila " +
            "o columna. También sirve mirar una fila o columna y notar si todas sus posiciones posibles " +
            "caen dentro de una sola caja.",
        whyItWorks = "El número sigue perteneciendo a las dos regiones a la vez. Si dentro de la caja solo puede " +
            "salir por una línea, entonces cualquier aparición del mismo candidato en esa línea pero fuera " +
            "de la caja se contradice con esa obligación.",
        commonMistakes = listOf(
            "No alcanza con que el candidato aparezca en la caja: todas sus posiciones posibles deben estar alineadas.",
            "La eliminación ocurre fuera de la región que encierra el candidato, no sobre las casillas que forman el patrón."
        ),
        practicalChecklist = listOf(
            "Un candidato.",
            "Una caja y una línea conectadas.",
            "Todas las posiciones posibles alineadas.",
            "Eliminaciones fuera del grupo que fuerza la alineación."
        ),
        sourceUrl = "https://www.sudokuwiki.org/Intersection_Removal",
        examples = TechniqueTutorialFixtures.forTechnique("intersection_removal"),
        accent = SudokuPalette.TextAccent,
        icon = Icons.Default.Psychology
    ),
    TechniqueUiModel(
        id = "hidden_pair",
        title = "Hidden Pair",
        level = "Medium",
        summary = "Dos candidatos solo pueden vivir en las mismas dos casillas.",
        idea = "Hidden Pair se parece a Naked Pair, pero se detecta mirando números, no casillas. " +
            "Las dos casillas pueden tener candidatos extra, por eso el patrón no salta a la vista: " +
            "lo importante es que dos números solo aparecen en esas mismas dos posiciones.",
        rule = "Si dos candidatos de una región aparecen únicamente en las mismas dos casillas, " +
            "esas casillas quedan reservadas para ese par. Todos los demás candidatos dentro de esas " +
            "dos casillas se pueden borrar.",
        steps = listOf(
            "Elegí una fila, columna o caja y mirá candidato por candidato.",
            "Buscá dos números que tengan exactamente las mismas dos ubicaciones posibles.",
            "Confirmá que esos números no aparezcan en otra casilla de esa región.",
            "En esas dos casillas, dejá solo el par escondido y borrá los candidatos sobrantes."
        ),
        result = "A diferencia de Naked Pair, la limpieza ocurre dentro de las casillas marcadas. " +
            "Después de borrar candidatos extra, esas casillas suelen transformarse en un par claro.",
        sourceUrl = "https://www.sudokuwiki.org/Hidden_Candidates",
        examples = TechniqueTutorialFixtures.forTechnique("hidden_pair"),
        accent = SudokuPalette.TextAccent,
        icon = Icons.Default.Psychology
    ),
    TechniqueUiModel(
        id = "naked_triple",
        title = "Naked Triple",
        level = "Hard",
        summary = "Tres casillas contienen solo tres candidatos combinados.",
        idea = "Naked Triple es una reserva de tres casillas. No hace falta que las tres casillas " +
            "tengan los tres candidatos escritos; puede ser 3-7, 7-9 y 3-9. Lo que importa es que, " +
            "al unirlas, solo aparezcan tres números distintos.",
        rule = "Si tres casillas de una misma región contienen únicamente tres candidatos combinados, " +
            "esos tres números quedan reservados para esas casillas y se eliminan del resto de la región.",
        steps = listOf(
            "Buscá tres casillas de una misma región con dos o tres candidatos cada una.",
            "Uní mentalmente sus candidatos y verificá que el conjunto total tenga exactamente tres números.",
            "Confirmá que ninguna de las tres casillas tenga un cuarto candidato.",
            "Quitá esos tres números de cualquier otra casilla de la región."
        ),
        result = "Produce eliminaciones más amplias que un par. No define el orden de los tres números, " +
            "pero sí asegura que el resto de la región no puede usarlos.",
        whatToWatch = "Buscá tres casillas vacías dentro de una misma fila, columna o caja. Lo importante no es " +
            "que todas tengan los mismos candidatos, sino que entre las tres solo aparezcan tres números " +
            "distintos.",
        whyItWorks = "Esas tres casillas necesitan tres valores y solo pueden usar esos tres candidatos. " +
            "Como el grupo " +
            "queda completo dentro de la región, ninguna otra casilla de esa misma región puede usar esos números.",
        commonMistakes = listOf(
            "Incluir una casilla con un cuarto candidato rompe el triple.",
            "Mezclar casillas de distintas regiones no sirve: las tres deben compartir fila, columna o caja."
        ),
        practicalChecklist = listOf(
            "Tres casillas en la misma región.",
            "Dos o tres candidatos por casilla.",
            "Tres candidatos distintos en total.",
            "Eliminar esos candidatos del resto de la región."
        ),
        sourceUrl = "https://www.sudokuwiki.org/Naked_Candidates",
        examples = TechniqueTutorialFixtures.forTechnique("naked_triple"),
        accent = SudokuPalette.MentorGold,
        icon = Icons.Default.Bolt
    ),
    TechniqueUiModel(
        id = "x_wing",
        title = "X-Wing",
        level = "Hard",
        summary = "Un candidato forma un rectángulo lógico entre dos filas y dos columnas.",
        idea = "X-Wing trabaja con un solo candidato y cuatro esquinas. Si en dos filas el candidato " +
            "solo puede ocupar las mismas dos columnas, entonces esas cuatro posiciones forman un " +
            "rectángulo: en una fila irá en una columna y en la otra fila irá en la otra.",
        rule = "Si dos filas tienen un candidato exactamente en las mismas dos columnas, ese candidato " +
            "se elimina de esas columnas en las demás filas. La misma lógica funciona intercambiando " +
            "filas por columnas.",
        steps = listOf(
            "Elegí un candidato y revisá sus posiciones fila por fila.",
            "Buscá dos filas donde ese candidato aparezca solo en dos columnas.",
            "Confirmá que las dos filas usen exactamente las mismas columnas.",
            "Eliminá ese candidato de esas columnas en todas las demás filas."
        ),
        result = "No importa cuál de las cuatro esquinas termine siendo correcta: el rectángulo garantiza " +
            "que las columnas o filas cruzadas ya están cubiertas por esas dos líneas.",
        whatToWatch = "Elegí un solo candidato y mirá sus posiciones por fila. Si dos filas tienen ese candidato " +
            "exactamente en las mismas dos columnas, esas cuatro casillas son el rectángulo. También podés " +
            "hacer la lectura inversa: dos columnas con las mismas dos filas.",
        whyItWorks = "En cada una de las dos filas debe ir una copia del candidato. Como ambas filas solo tienen " +
            "las mismas dos columnas disponibles, el candidato ocupará una esquina en cada fila. Eso cubre " +
            "las dos columnas del rectángulo, así que el mismo candidato no puede aparecer en otras casillas " +
            "de esas columnas.",
        commonMistakes = listOf(
            "No alcanza con ver cuatro candidatos formando un rectángulo: en las líneas base no puede haber un tercer lugar posible.",
            "Las eliminaciones no se hacen dentro de las cuatro esquinas, sino fuera del rectángulo en las líneas cruzadas.",
            "Hay que trabajar con un solo candidato; mezclar números distintos invalida el patrón."
        ),
        practicalChecklist = listOf(
            "Un candidato fijo.",
            "Dos filas con exactamente dos posiciones posibles cada una.",
            "Las dos filas usan las mismas dos columnas.",
            "Eliminar ese candidato de las demás casillas de esas columnas."
        ),
        sourceUrl = "https://www.sudokuwiki.org/X_Wing_Strategy",
        examples = TechniqueTutorialFixtures.forTechnique("x_wing"),
        accent = SudokuPalette.MentorGold,
        icon = Icons.Default.Bolt
    ),
    TechniqueUiModel(
        id = "y_wing",
        title = "Y-Wing",
        level = "Expert",
        summary = "Tres casillas conectadas fuerzan una eliminación por cadena corta.",
        idea = "Y-Wing es una cadena corta con tres casillas de dos candidatos. La casilla pivote ve " +
            "a dos alas: una comparte un candidato con la pivote y la otra comparte el otro. Las alas, " +
            "a su vez, comparten un tercer candidato entre ellas.",
        rule = "Si la pivote es A, una ala fuerza C. Si la pivote es B, la otra ala fuerza C. " +
            "Como en ambos caminos aparece C en una de las alas, cualquier casilla que vea a ambas alas " +
            "no puede contener C.",
        steps = listOf(
            "Buscá una pivote con exactamente dos candidatos, por ejemplo 2 y 9.",
            "Encontrá un ala que vea a la pivote y tenga 2 más otro candidato.",
            "Encontrá otra ala que vea a la pivote y tenga 9 más ese mismo otro candidato.",
            "Quitá el candidato compartido de las casillas que vean simultáneamente a las dos alas."
        ),
        result = "Es una técnica de eliminación, no de colocación directa. Su fuerza está en conectar regiones: " +
            "las tres casillas no necesitan estar todas en la misma fila, columna o caja.",
        whatToWatch = "Buscá una casilla pivote con dos candidatos. Después buscá dos alas que vean a la pivote: " +
            "una comparte el primer candidato y la otra comparte el segundo. Las alas además deben compartir " +
            "un tercer candidato entre ellas.",
        whyItWorks = "La pivote solo tiene dos caminos. Si toma el primer candidato, una de las alas queda forzada " +
            "al candidato compartido. Si toma el segundo candidato, queda forzada la otra ala. En ambos caminos, " +
            "una de las alas termina usando ese candidato compartido, por eso se puede quitar de cualquier " +
            "casilla que vea simultáneamente a las dos alas.",
        commonMistakes = listOf(
            "Las alas no necesitan verse entre sí; la casilla eliminada sí debe ver a ambas alas.",
            "La pivote debe tener exactamente dos candidatos. Si tiene tres o más, la cadena no fuerza nada.",
            "El candidato que se elimina es el que comparten las dos alas, no los candidatos de la pivote."
        ),
        practicalChecklist = listOf(
            "Pivote con candidatos A y B.",
            "Un ala con A y C que vea a la pivote.",
            "Otra ala con B y C que vea a la pivote.",
            "Eliminar C solo de casillas que vean ambas alas."
        ),
        sourceUrl = "https://www.sudokuwiki.org/Y_Wing_Strategy",
        examples = TechniqueTutorialFixtures.forTechnique("y_wing"),
        accent = SudokuPalette.MentorPurple,
        icon = Icons.Default.SportsScore
    )
)

@Preview(showBackground = true, backgroundColor = 0xFF010413)
@Composable
fun TechniquesScreenPreview() {
    TechniquesScreen(onTechniqueClick = {}, onBackClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF010413)
@Composable
fun TechniqueDetailScreenPreview() {
    TechniqueDetailScreen(techniqueId = "naked_pair", onBackClick = {})
}
