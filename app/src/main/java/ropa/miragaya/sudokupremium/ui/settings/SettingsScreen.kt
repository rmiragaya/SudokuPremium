package ropa.miragaya.sudokupremium.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.ui.component.MentorButton
import ropa.miragaya.sudokupremium.ui.model.PremiumStatusMessage
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.ui.theme.SudokuPremiumTheme

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onOpenPremiumClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onOpenPremiumClick = onOpenPremiumClick,
        onDebugResetPremiumClick = viewModel::onDebugResetPremiumClick,
        modifier = modifier
    )
}

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PremiumContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onPurchaseClick = { viewModel.onPurchasePremiumClick(context.findActivity()) },
        onRestoreClick = viewModel::onRestorePremiumClick,
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onOpenPremiumClick: () -> Unit,
    onDebugResetPremiumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        SettingsTopBar(title = stringResource(R.string.settings_title), onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (BuildConfig.DEBUG) {
                SettingsSectionTitle(stringResource(R.string.settings_section_appearance))
                SettingsInfoRow(
                    icon = Icons.Default.Palette,
                    title = stringResource(R.string.settings_board_style_title),
                    description = stringResource(R.string.settings_board_style_description)
                )
            }

            SettingsSectionTitle(stringResource(R.string.settings_section_premium))
            SettingsPremiumRow(
                isPremium = uiState.isPremium,
                premiumStatusMessage = uiState.premiumStatusMessage,
                onOpenPremiumClick = onOpenPremiumClick
            )

            if (BuildConfig.DEBUG) {
                SettingsSectionTitle(stringResource(R.string.settings_section_debug))
                SettingsDebugRow(onDebugResetPremiumClick = onDebugResetPremiumClick)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PremiumContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onRestoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        SettingsTopBar(title = stringResource(R.string.premium_title), onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val boardSize = minOf(maxWidth * PREMIUM_HERO_BOARD_WIDTH_FRACTION, maxHeight)
                PremiumHeroBoard(modifier = Modifier.size(boardSize))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isPremium) {
                        stringResource(R.string.premium_headline_active)
                    } else {
                        stringResource(R.string.premium_headline_locked)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (uiState.isPremium) {
                        stringResource(R.string.premium_body_active)
                    } else {
                        stringResource(R.string.premium_body_locked)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SudokuPalette.TextSecondary,
                    textAlign = TextAlign.Center
                )

                PremiumBenefits()

                uiState.premiumStatusMessage?.let { message ->
                    Text(
                        text = stringResource(message.stringRes),
                        color = SudokuPalette.TextAccent,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                if (!uiState.isPremium) {
                    PremiumUnlockButton(
                        isLoading = uiState.isPurchaseLoading,
                        onClick = onPurchaseClick
                    )

                    TextButton(onClick = onRestoreClick) {
                        Text(stringResource(R.string.premium_already_bought), color = SudokuPalette.TextAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(title: String, onBackClick: () -> Unit) {
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
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        color = SudokuPalette.TextSecondary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, description: String) {
    SettingsRowContainer {
        SettingsIcon(imageVector = icon)
        Column(modifier = Modifier.weight(1f)) {
            SettingsRowText(title = title, description = description)
        }
    }
}

@Composable
private fun SettingsPremiumRow(
    isPremium: Boolean,
    premiumStatusMessage: PremiumStatusMessage?,
    onOpenPremiumClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsIcon(
                    imageVector = if (isPremium) Icons.Default.CheckCircle else Icons.Default.School,
                    accent = if (isPremium) SudokuPalette.CellHintBorder else SudokuPalette.TextAccent
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isPremium) {
                            stringResource(R.string.premium_active)
                        } else {
                            stringResource(R.string.premium_title)
                        },
                        color = SudokuPalette.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPremium) {
                            stringResource(R.string.premium_card_active_description)
                        } else {
                            stringResource(R.string.premium_card_description)
                        },
                        color = SudokuPalette.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            premiumStatusMessage?.let { message ->
                Text(
                    text = stringResource(message.stringRes),
                    color = SudokuPalette.TextAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!isPremium) {
                PremiumUnlockButton(
                    isLoading = false,
                    onClick = onOpenPremiumClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private val PremiumStatusMessage.stringRes: Int
    get() = when (this) {
        PremiumStatusMessage.PURCHASE_NOT_STARTED -> R.string.premium_status_purchase_not_started
        PremiumStatusMessage.PREMIUM_ACTIVATED -> R.string.premium_status_activated
        PremiumStatusMessage.PURCHASE_PENDING -> R.string.premium_status_purchase_pending
        PremiumStatusMessage.PURCHASE_CANCELED -> R.string.premium_status_purchase_canceled
        PremiumStatusMessage.PREMIUM_ACTIVATION_FAILED -> R.string.premium_status_activation_failed
    }

@Composable
private fun SettingsDebugRow(onDebugResetPremiumClick: () -> Unit) {
    SettingsRowContainer {
        SettingsIcon(imageVector = Icons.Default.Settings, accent = SudokuPalette.CellEliminationBorder)
        Column(modifier = Modifier.weight(1f)) {
            SettingsRowText(
                title = stringResource(R.string.settings_debug_reset_premium_title),
                description = stringResource(R.string.settings_debug_reset_premium_description)
            )
        }
        TextButton(onClick = onDebugResetPremiumClick) {
            Text(stringResource(R.string.settings_debug_reset), color = SudokuPalette.CellEliminationBorder)
        }
    }
}

@Composable
private fun SettingsRowContainer(content: @Composable RowScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.84f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsIcon(imageVector: ImageVector, accent: Color = SudokuPalette.TextAccent) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.14f)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = accent,
            modifier = Modifier
                .padding(10.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun SettingsRowText(title: String, description: String) {
    Text(
        text = title,
        color = SudokuPalette.TextPrimary,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = description,
        color = SudokuPalette.TextSecondary,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun PremiumBenefits() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumBenefitRow(
                text = stringResource(R.string.premium_benefit_unlimited_hints),
                modifier = Modifier.weight(1f)
            )
            PremiumBenefitRow(
                text = stringResource(R.string.premium_benefit_no_ads),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumBenefitRow(
                text = stringResource(R.string.premium_benefit_practice),
                modifier = Modifier.weight(1f)
            )
            PremiumBenefitRow(
                text = stringResource(R.string.premium_benefit_support),
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PremiumUnlockButton(isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    MentorButton(
        text = if (isLoading) {
            stringResource(R.string.premium_purchase_loading)
        } else {
            stringResource(R.string.premium_unlock)
        },
        onClick = onClick,
        enabled = !isLoading,
        isLoading = isLoading,
        showGlow = true,
        modifier = modifier,
        height = 50.dp
    )
}

@Composable
private fun PremiumBenefitRow(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.CheckCircle
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SudokuPalette.HomePanel.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SudokuPalette.TextAccent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = SudokuPalette.TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PremiumHeroBoard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(22.dp),
        color = SudokuPalette.BoardBackground,
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .drawWithContent {
                    drawContent()
                    val cellSize = size.width / 9f
                    val thinColor = SudokuPalette.GridLine
                    val thickColor = SudokuPalette.TextAccent.copy(alpha = 0.72f)

                    for (index in 1 until 9) {
                        val lineColor = if (index % 3 == 0) thickColor else thinColor
                        val stroke = if (index % 3 == 0) 3.dp.toPx() else 1.dp.toPx()
                        val position = cellSize * index

                        drawLine(
                            color = lineColor,
                            start = Offset(position, 0f),
                            end = Offset(position, size.height),
                            strokeWidth = stroke
                        )
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, position),
                            end = Offset(size.width, position),
                            strokeWidth = stroke
                        )
                    }
                },
            verticalArrangement = Arrangement.spacedBy(HeroBoardCellSpacing)
        ) {
            val numbers = premiumBoardNumbers
            repeat(9) { row ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(HeroBoardCellSpacing)
                ) {
                    repeat(9) { col ->
                        val cellIndex = row * 9 + col
                        PremiumHeroCell(
                            value = numbers[cellIndex],
                            isHighlighted = cellIndex in premiumHighlightCells,
                            isElimination = cellIndex in premiumEliminationCells,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHeroCell(
    value: Int?,
    isHighlighted: Boolean,
    isElimination: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isElimination -> SudokuPalette.CellEliminationBg
        isHighlighted -> SudokuPalette.CellHint
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(5.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (value != null) {
            Text(
                text = value.toString(),
                color = if (isHighlighted) SudokuPalette.CellHintBorder else SudokuPalette.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        } else if (isHighlighted || isElimination) {
            Text(
                text = if (isElimination) "3" else "3 7",
                color = if (isElimination) SudokuPalette.CellEliminationBorder else SudokuPalette.CellHintBorder,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private val premiumBoardNumbers = listOf(
    8, null, 5, null, 9, null, null, 3, null,
    null, 3, null, null, null, null, null, 6, 9,
    9, null, 2, null, 6, 3, 1, 5, 8,
    null, 2, null, 8, null, 4, 5, 9, null,
    8, 5, 1, 9, null, 7, null, 4, 6,
    3, 9, 4, 6, null, 5, 8, 7, null,
    5, 6, 3, null, 4, null, 9, 8, 7,
    2, null, null, null, null, null, null, 1, 5,
    null, 1, null, null, 5, null, null, 2, null
)

private val premiumHighlightCells = setOf(3, 12)
private val premiumEliminationCells = setOf(21)
private val HeroBoardCellSpacing: Dp = 1.dp
private const val PREMIUM_HERO_BOARD_WIDTH_FRACTION = 0.86f

@Preview
@Composable
private fun SettingsScreenPreview() {
    SudokuPremiumTheme {
        SettingsContent(
            uiState = SettingsUiState(),
            onBackClick = {},
            onOpenPremiumClick = {},
            onDebugResetPremiumClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun PremiumScreenPreview() {
    SudokuPremiumTheme {
        PremiumContent(
            uiState = SettingsUiState(),
            onBackClick = {},
            onPurchaseClick = {},
            onRestoreClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
