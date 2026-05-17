package ropa.miragaya.sudokupremium.ui.game

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import ropa.miragaya.sudokupremium.domain.model.Difficulty
import ropa.miragaya.sudokupremium.domain.model.SudokuHint
import ropa.miragaya.sudokupremium.ui.component.MentorButton
import ropa.miragaya.sudokupremium.ui.game.component.GameWonDialog
import ropa.miragaya.sudokupremium.ui.game.component.HintOverlayCard
import ropa.miragaya.sudokupremium.ui.game.component.MistakeDialog
import ropa.miragaya.sudokupremium.ui.game.component.SudokuDecodingBoard
import ropa.miragaya.sudokupremium.ui.home.DifficultySelectionSheet
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette
import ropa.miragaya.sudokupremium.util.toFormattedTime

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    onOpenPremiumClick: () -> Unit,
    onOpenTechniqueClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    val view = LocalView.current
    var showVictoryDifficultySheet by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val debugDumpLabel = stringResource(R.string.debug_json_label)
    val debugDumpCopiedMessage = stringResource(R.string.debug_json_copied)
    val onGetDebugDumpClick = if (BuildConfig.DEBUG) {
        getDebugDump(viewModel, context, debugDumpLabel, debugDumpCopiedMessage)
    } else {
        {}
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            showVictoryDialog = true
        } else {
            showVictoryDialog = false
            showVictoryDifficultySheet = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.resumeTimer()
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseTimer()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (uiState.showMistakeError) {
        MistakeDialog(
            mistakeCount = uiState.mistakeCount,
            onDismiss = viewModel::onDismissMistakeDialog,
            onConfirm = viewModel::onRevealMistakes
        )
    }

    if (uiState.showHintLimitSheet) {
        HintLimitDialog(
            freeHintsPerGame = uiState.freeHintsPerGame,
            isLoading = uiState.isRewardedHintLoading,
            showError = uiState.showRewardedHintError,
            onWatchAd = { viewModel.onWatchRewardedHintAdClick(activity) },
            onUnlockPremium = {
                viewModel.onDismissHintLimitSheet()
                onOpenPremiumClick()
            },
            onDismiss = viewModel::onDismissHintLimitSheet
        )
    }

    if (uiState.showHowToPlayDialog) {
        HowToPlayDialog(
            isFirstGameIntro = uiState.isHowToPlayFirstGameIntro,
            canStartGuidedTutorial = uiState.isHowToPlayFirstGameIntro &&
                uiState.difficulty == Difficulty.EASY &&
                !uiState.isComplete &&
                !uiState.isLoading,
            onStartGuidedTutorial = viewModel::onStartGuidedTutorial,
            onDismiss = viewModel::onDismissHowToPlayDialog
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameContent(
            uiState = uiState,
            onCellClick = { cellId ->
                if (!uiState.isComplete) viewModel.onCellClicked(cellId)
            },
            onNumberInput = { number ->
                if (!uiState.isComplete) {
                    performSudokuHaptic(context, view, uiState.hapticsEnabled, HapticFeedbackConstants.KEYBOARD_TAP)
                    viewModel.onNumberInput(number)
                }
            },
            onDeleteInput = {
                if (!uiState.isComplete) {
                    performSudokuHaptic(context, view, uiState.hapticsEnabled, HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.onDeleteInput()
                }
            },
            onToggleNoteMode = {
                if (!uiState.isComplete) {
                    performSudokuHaptic(context, view, uiState.hapticsEnabled, HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.toggleNoteMode()
                }
            },
            onUndo = {
                if (!uiState.isComplete) {
                    performSudokuHaptic(context, view, uiState.hapticsEnabled, HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.onUndo()
                }
            },
            onBackClick = onBackClick,
            onHintClick = {
                performSudokuHaptic(context, view, uiState.hapticsEnabled, HapticFeedbackConstants.VIRTUAL_KEY)
                viewModel.onRequestHint()
            },
            onGetDebugDumpClick = onGetDebugDumpClick,
            onCrashlyticsTestCrashClick = viewModel::onCrashlyticsTestCrashClick,
            onDebugFillCandidatesClick = viewModel::onDebugFillCandidatesClick,
            onDebugPrepareVictoryClick = viewModel::onDebugPrepareVictoryClick,
            onDebugResetPremiumClick = viewModel::onDebugResetPremiumClick,
            onSettingsClick = onOpenSettingsClick,
            onOpenTechniquesClick = onOpenTechniquesClick,
            onHowToPlayClick = viewModel::onHowToPlayMenuClick,
            onOpenTechniqueClick = onOpenTechniqueClick,
            currentHintIndex = uiState.currentHintIndex,
            totalHints = uiState.activeHints.size,
            onDismissHint = viewModel::onDismissHint,
            onNextHint = viewModel::onNextHint,
            onPrevHint = viewModel::onPrevHint,
            onSkipGuidedTutorial = viewModel::onSkipGuidedTutorial,
            onNextGuidedTutorialStep = viewModel::onNextGuidedTutorialStep,
            onVictoryNewGameClick = {
                showVictoryDialog = false
                showVictoryDifficultySheet = true
            },
            modifier = modifier
        )

        if (uiState.isComplete) {
            if (showVictoryDifficultySheet) {
                DifficultySelectionSheet(
                    onDismiss = { showVictoryDifficultySheet = false },
                    onDifficultySelected = { difficulty ->
                        showVictoryDifficultySheet = false
                        viewModel.startNewGame(difficulty)
                    }
                )
            } else if (showVictoryDialog) {
                GameWonDialog(
                    difficulty = uiState.difficulty.name,
                    elapsedTimeSeconds = uiState.elapsedTimeSeconds,
                    hintsUsed = uiState.hintsUsed,
                    onStartNewGame = {
                        showVictoryDialog = false
                        showVictoryDifficultySheet = true
                    },
                    onDismiss = { showVictoryDialog = false }
                )
            }
        }
    }
}

@Composable
private fun getDebugDump(
    viewModel: GameViewModel,
    context: Context,
    clipLabel: String,
    copiedMessage: String
): () -> Unit = {
    val dumpString = viewModel.getDebugDump()

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(clipLabel, dumpString)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
}

private fun performSudokuHaptic(context: Context, view: View, enabled: Boolean, feedbackConstant: Int) {
    if (!enabled) return

    val didPerformViewHaptic = view.performHapticFeedback(feedbackConstant)
    if (didPerformViewHaptic) return

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (!vibrator.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                SUDOKU_HAPTIC_DURATION_MILLIS,
                SUDOKU_HAPTIC_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(SUDOKU_HAPTIC_DURATION_MILLIS)
    }
}

@Composable
private fun HintLimitDialog(
    freeHintsPerGame: Int,
    isLoading: Boolean,
    showError: Boolean,
    onWatchAd: () -> Unit,
    onUnlockPremium: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(28.dp),
            color = SudokuPalette.HomePanel,
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.hint_limit_title),
                    color = SudokuPalette.TextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.hint_limit_message, freeHintsPerGame),
                    color = SudokuPalette.TextSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = stringResource(R.string.hint_limit_ad_error),
                        color = SudokuPalette.TextError,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                MentorButton(
                    text = stringResource(R.string.hint_limit_watch_ad),
                    onClick = onWatchAd,
                    enabled = !isLoading,
                    isLoading = isLoading,
                    height = 50.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onUnlockPremium) {
                    Text(stringResource(R.string.premium_unlock), color = SudokuPalette.TextAccent)
                }

                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_now_not), color = SudokuPalette.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun HowToPlayDialog(
    isFirstGameIntro: Boolean,
    canStartGuidedTutorial: Boolean,
    onStartGuidedTutorial: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SudokuPalette.HomePanel,
        title = {
            Text(
                text = if (isFirstGameIntro) {
                    stringResource(R.string.how_to_play_intro_title)
                } else {
                    stringResource(R.string.how_to_play_title)
                },
                color = SudokuPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isFirstGameIntro) {
                        stringResource(R.string.how_to_play_intro_body)
                    } else {
                        stringResource(R.string.how_to_play_body)
                    },
                    color = SudokuPalette.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                HowToPlayRule(
                    title = stringResource(R.string.how_to_play_goal_title),
                    body = stringResource(R.string.how_to_play_goal_body)
                )
                HowToPlayRule(
                    title = stringResource(R.string.how_to_play_rule_title),
                    body = stringResource(R.string.how_to_play_rule_body)
                )
                HowToPlayRule(
                    title = stringResource(R.string.how_to_play_app_title),
                    body = stringResource(R.string.how_to_play_app_body)
                )
            }
        },
        confirmButton = {
            if (canStartGuidedTutorial) {
                MentorButton(
                    text = stringResource(R.string.how_to_play_start_guided),
                    onClick = onStartGuidedTutorial,
                    height = 46.dp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isFirstGameIntro) {
                        stringResource(R.string.how_to_play_skip)
                    } else {
                        stringResource(R.string.action_close)
                    },
                    color = SudokuPalette.TextSecondary
                )
            }
        }
    )
}

@Composable
private fun HowToPlayRule(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            color = SudokuPalette.TextPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            color = SudokuPalette.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Difficulty.displayName(): String {
    return when (this) {
        Difficulty.EASY -> stringResource(R.string.difficulty_easy_title)
        Difficulty.MEDIUM -> stringResource(R.string.difficulty_medium_title)
        Difficulty.HARD -> stringResource(R.string.difficulty_hard_title)
        Difficulty.EXPERT -> stringResource(R.string.difficulty_expert_title)
    }
}

@Composable
fun GameContent(
    uiState: GameUiState,
    onCellClick: (Int) -> Unit,
    onNumberInput: (Int) -> Unit,
    onDeleteInput: () -> Unit,
    onToggleNoteMode: () -> Unit,
    onUndo: () -> Unit,
    onBackClick: () -> Unit,
    onHintClick: () -> Unit,
    onGetDebugDumpClick: () -> Unit,
    onCrashlyticsTestCrashClick: () -> Unit,
    onDebugFillCandidatesClick: () -> Unit,
    onDebugPrepareVictoryClick: () -> Unit,
    onDebugResetPremiumClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onOpenTechniqueClick: (String) -> Unit,
    currentHintIndex: Int,
    totalHints: Int,
    onDismissHint: () -> Unit,
    onNextHint: () -> Unit,
    onPrevHint: () -> Unit,
    onSkipGuidedTutorial: () -> Unit,
    onNextGuidedTutorialStep: () -> Unit,
    onVictoryNewGameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val guidedTutorial = uiState.guidedTutorial
    val activeHint = uiState.activeHint
    val boardHint = guidedTutorial?.currentHint ?: activeHint
    val isGuidedTutorialActive = guidedTutorial != null
    val shouldScrollContent = activeHint != null && !isGuidedTutorialActive
    val contentScrollState = rememberScrollState()
    val boardTopSpacerHeight by animateDpAsState(
        targetValue = if (boardHint != null) 0.dp else 28.dp,
        animationSpec = tween(320),
        label = "BoardTopSpacer"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = SudokuPalette.MainGradient)
    ) {
        GameTopBar(
            difficulty = uiState.difficulty.displayName(),
            onBackClick = onBackClick,
            onGetDebugDumpClick = onGetDebugDumpClick,
            onCrashlyticsTestCrashClick = onCrashlyticsTestCrashClick,
            onDebugFillCandidatesClick = onDebugFillCandidatesClick,
            onDebugPrepareVictoryClick = onDebugPrepareVictoryClick,
            onDebugResetPremiumClick = onDebugResetPremiumClick,
            onSettingsClick = onSettingsClick,
            onOpenTechniquesClick = onOpenTechniquesClick,
            onHowToPlayClick = onHowToPlayClick
        )

        GameTimerPill(
            elapsedTimeSeconds = uiState.elapsedTimeSeconds,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(animationSpec = tween(320))
                .padding(16.dp)
                .then(
                    if (shouldScrollContent) Modifier.verticalScroll(contentScrollState) else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(boardTopSpacerHeight))

            Box(
                modifier = if (isGuidedTutorialActive) {
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth()
                },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = uiState.isLoading,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(600)))
                            .togetherWith(
                                fadeOut(animationSpec = tween(600))
                            )
                    },
                    label = "BoardTransition"
                ) { isLoading ->
                    if (isLoading) {
                        SudokuDecodingBoard()
                    } else {
                        val displayBoard = boardHint?.stepBoard ?: uiState.board
                        val boardModifier = if (isGuidedTutorialActive) {
                            Modifier
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .fillMaxSize()
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        }

                        SudokuBoardView(
                            board = displayBoard,
                            selectedCellId = uiState.selectedCellId,
                            highlightedIds = uiState.highlightedCellIds,
                            sameValueIds = uiState.sameValueCellIds,
                            activeHint = boardHint,
                            onCellClick = onCellClick,
                            modifier = boardModifier
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (boardHint != null) 10.dp else 30.dp))

            AnimatedVisibility(
                visible = guidedTutorial != null,
                enter = fadeIn(animationSpec = tween(260)) +
                    slideInVertically(
                        animationSpec = tween(320),
                        initialOffsetY = { it / 3 }
                    ),
                exit = fadeOut(animationSpec = tween(180)) +
                    slideOutVertically(
                        animationSpec = tween(220),
                        targetOffsetY = { it / 5 }
                    )
            ) {
                guidedTutorial?.let { tutorial ->
                    GuidedTutorialCard(
                        tutorial = tutorial,
                        inputMessage = uiState.tutorialInputMessage,
                        onSkip = onSkipGuidedTutorial,
                        onNext = onNextGuidedTutorialStep,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            AnimatedVisibility(
                visible = activeHint != null && guidedTutorial == null,
                enter = fadeIn(animationSpec = tween(260)) +
                    slideInVertically(
                        animationSpec = tween(320),
                        initialOffsetY = { it / 3 }
                    ),
                exit = fadeOut(animationSpec = tween(180)) +
                    slideOutVertically(
                        animationSpec = tween(220),
                        targetOffsetY = { it / 5 }
                    )
            ) {
                activeHint?.let { hint ->
                    HintOverlayCard(
                        hint = hint,
                        currentStep = currentHintIndex,
                        totalSteps = totalHints,
                        onDismiss = onDismissHint,
                        onNext = onNextHint,
                        onPrev = onPrevHint,
                        onTechniqueClick = { onOpenTechniqueClick(hint.strategyName.toTechniqueId()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.isComplete && !uiState.isLoading) {
                GameCompleteActions(
                    onNewGameClick = onVictoryNewGameClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else if (guidedTutorial?.isMoveStep == true && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(10.dp))

                NumberPad(
                    onNumberClick = onNumberInput,
                    onDeleteClick = onDeleteInput,
                    completedNumbers = uiState.completedNumbers,
                    highlightedNumber = guidedTutorial.currentHint.valueToSet
                )

                Spacer(modifier = Modifier.height(6.dp))
            } else if (activeHint == null && !uiState.isLoading) {
                GameControls(
                    isNoteMode = uiState.isNoteMode,
                    onToggleNoteMode = onToggleNoteMode,
                    onUndo = onUndo,
                    onHintClick = onHintClick,
                    enabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                NumberPad(
                    onNumberClick = onNumberInput,
                    onDeleteClick = onDeleteInput,
                    completedNumbers = uiState.completedNumbers
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GameCompleteActions(onNewGameClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = SudokuPalette.HomePanel,
        border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.victory_title),
                color = SudokuPalette.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            MentorButton(
                text = stringResource(R.string.home_new_game),
                onClick = onNewGameClick,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GuidedTutorialCard(
    tutorial: GuidedTutorialUiState,
    inputMessage: String?,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = SudokuPalette.HomePanel,
        border = BorderStroke(1.dp, SudokuPalette.CellHintBorder.copy(alpha = 0.42f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (tutorial.isMoveStep) {
                        stringResource(
                            R.string.guided_tutorial_step_count,
                            tutorial.currentStep,
                            tutorial.totalSteps
                        )
                    } else {
                        stringResource(R.string.how_to_play_title)
                    },
                    color = SudokuPalette.CellHintBorder,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onSkip) {
                    Text(stringResource(R.string.how_to_play_skip), color = SudokuPalette.TextSecondary)
                }
            }

            Text(
                text = tutorial.currentHint.description,
                color = SudokuPalette.TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (tutorial.isMoveStep) {
                Text(
                    text = stringResource(
                        R.string.guided_tutorial_instruction,
                        tutorial.currentHint.valueToSet ?: 0
                    ),
                    color = SudokuPalette.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onNext) {
                        Text(stringResource(R.string.action_next), color = SudokuPalette.CellHintBorder)
                    }
                }
            }
            if (inputMessage != null) {
                Text(
                    text = inputMessage,
                    color = SudokuPalette.CellHintBorder,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SudokuBoardView(
    board: Board,
    selectedCellId: Int?,
    highlightedIds: Set<Int>,
    sameValueIds: Set<Int>,
    activeHint: SudokuHint?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(SudokuPalette.BoardBackground)
            .border(2.dp, SudokuPalette.GridLine, RoundedCornerShape(15.dp))
            .drawWithContent {
                drawContent()

                activeHint?.highlightBoxes?.forEach { boxIndex ->
                    val boxRow = boxIndex / 3
                    val boxCol = boxIndex % 3
                    val cellSize = size.width / 9f
                    val strokeWidth = 3.dp.toPx()

                    drawRect(
                        color = SudokuPalette.CellHintBorder,
                        topLeft = Offset(
                            x = boxCol * 3 * cellSize + strokeWidth / 2,
                            y = boxRow * 3 * cellSize + strokeWidth / 2
                        ),
                        size = Size(
                            width = 3 * cellSize - strokeWidth,
                            height = 3 * cellSize - strokeWidth
                        ),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
    ) {
        board.rows.forEachIndexed { rowIndex, rowCells ->
            Row(modifier = Modifier.weight(1f)) {
                rowCells.forEachIndexed { colIndex, cell ->

                    val rightBorder = if (colIndex == 2 || colIndex == 5) 2.dp else 0.5.dp
                    val bottomBorder = if (rowIndex == 2 || rowIndex == 5) 2.dp else 0.5.dp

                    val isHighlighted = highlightedIds.contains(cell.id)
                    val isSameValue = sameValueIds.contains(cell.id)

                    val isTargetCell = activeHint?.targetCellIndex == cell.id
                    val notesToRemoveInThisCell = activeHint?.notesToRemove?.get(cell.id) ?: emptyList()
                    val hasNotesToRemove = notesToRemoveInThisCell.isNotEmpty()
                    val isExplanationCell = activeHint?.highlightCells?.contains(cell.id) == true

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .drawWithContent {
                                drawContent()
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = rightBorder.toPx()
                                )
                                drawLine(
                                    color = SudokuPalette.GridLine,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = bottomBorder.toPx()
                                )
                            }
                    ) {
                        CellView(
                            cell = cell,
                            isSelected = cell.id == selectedCellId,
                            isHighlighted = isHighlighted,
                            isSameValue = isSameValue,
                            isHintTarget = isTargetCell || isExplanationCell,
                            isHintElimination = hasNotesToRemove,
                            notesToCrossOut = notesToRemoveInThisCell,
                            onClick = { onCellClick(cell.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CellView(
    cell: Cell,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isSameValue: Boolean,
    isHintTarget: Boolean, // Ahora esto significa "estoy involucrado en la pista de alguna forma"
    isHintElimination: Boolean,
    notesToCrossOut: List<Int> = emptyList(), // Nueva variable para saber qué notas pintar de rojo
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isHintTarget -> SudokuPalette.CellHint // Fondo doradito para celdas de la pista
        isHintElimination -> SudokuPalette.CellEliminationBg
        cell.isError -> SudokuPalette.CellErrorBg
        isSelected -> SudokuPalette.CellSelected
        isHighlighted -> SudokuPalette.CellHighlight
        isSameValue -> SudokuPalette.CellSelected
        else -> SudokuPalette.CellNormal
    }

    val textColor = when {
        cell.isError && !cell.isGiven -> SudokuPalette.TextError
        cell.isGiven -> SudokuPalette.TextPrimary
        else -> SudokuPalette.TextAccent
    }
    val weight = if (cell.isGiven) FontWeight.Bold else FontWeight.Medium

    val cellBorderModifier = when {
        isHintTarget && cell.value == null && notesToCrossOut.isEmpty() ->
            Modifier.border(2.dp, SudokuPalette.CellHintBorder)

        isHintElimination ->
            Modifier.border(1.dp, SudokuPalette.CellEliminationBorder)

        else -> Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .then(cellBorderModifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != null) {
            Text(
                text = cell.value.toString(),
                fontSize = 25.sp,
                fontWeight = weight,
                color = textColor
            )
        } else if (cell.notes.isNotEmpty()) {
            // Le pasamos al grid las notas que están marcadas para morir
            NotesGrid(notes = cell.notes, notesToCrossOut = notesToCrossOut)
        }
    }
}

@Composable
fun NotesGrid(
    notes: Set<Int>,
    // Recibimos la lista de la muerte.
    notesToCrossOut: List<Int> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { col ->
                    val number = row * 3 + col + 1

                    if (notes.contains(number)) {
                        // Si la nota está en la lista de borrado, la pintamos de rojo intenso
                        val isCrossedOut = notesToCrossOut.contains(number)
                        val noteColor = if (isCrossedOut) Color.Red else SudokuPalette.TextSecondary
                        val noteWeight = if (isCrossedOut) FontWeight.Bold else FontWeight.Normal

                        Text(
                            text = number.toString(),
                            fontSize = 8.sp,
                            color = noteColor,
                            fontWeight = noteWeight,
                            lineHeight = 8.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    completedNumbers: Set<Int>,
    highlightedNumber: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..5).forEach { number ->
                SudokuButton(
                    text = number.toString(),
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f),
                    enabled = !completedNumbers.contains(number),
                    isHighlighted = highlightedNumber == number
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (6..9).forEach { number ->
                SudokuButton(
                    text = number.toString(),
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f),
                    enabled = !completedNumbers.contains(number),
                    isHighlighted = highlightedNumber == number
                )
            }
            SudokuButton(
                text = stringResource(R.string.game_delete_short),
                onClick = onDeleteClick,
                isDestructive = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SudokuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDestructive: Boolean = false,
    isHighlighted: Boolean = false
) {
    val containerColor = when {
        isDestructive -> SudokuPalette.ButtonDestructive
        isHighlighted -> SudokuPalette.CellHintBorder
        else -> SudokuPalette.ButtonContainer
    }
    val contentColor = when {
        isDestructive -> SudokuPalette.ButtonDestructiveContent
        isHighlighted -> SudokuPalette.Night
        else -> SudokuPalette.ButtonContent
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameTopBar(
    difficulty: String,
    onBackClick: () -> Unit,
    onGetDebugDumpClick: () -> Unit,
    onCrashlyticsTestCrashClick: () -> Unit,
    onDebugFillCandidatesClick: () -> Unit,
    onDebugPrepareVictoryClick: () -> Unit,
    onDebugResetPremiumClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOpenTechniquesClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
            tint = SudokuPalette.TextSecondary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBackClick() }
                .padding(8.dp)
        )

        Text(
            text = difficulty,
            style = MaterialTheme.typography.titleMedium,
            color = SudokuPalette.TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = if (BuildConfig.DEBUG) {
                Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = onGetDebugDumpClick
                )
            } else {
                Modifier
            }
                .align(Alignment.Center)
        )

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.game_more_options),
                    tint = SudokuPalette.TextSecondary
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                containerColor = SudokuPalette.HomePanel
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.game_menu_settings)) },
                    colors = gameMenuItemColors(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onSettingsClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.game_menu_techniques)) },
                    colors = gameMenuItemColors(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onOpenTechniquesClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.game_menu_how_to_play)) },
                    colors = gameMenuItemColors(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onHowToPlayClick()
                    }
                )
                if (BuildConfig.DEBUG) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.game_menu_add_candidates)) },
                        colors = gameMenuItemColors(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDebugFillCandidatesClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.game_menu_prepare_victory)) },
                        colors = gameMenuItemColors(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDebugPrepareVictoryClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.game_menu_reset_premium)) },
                        colors = gameMenuItemColors(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDebugResetPremiumClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.game_menu_test_crashlytics)) },
                        colors = gameMenuItemColors(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onCrashlyticsTestCrashClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun gameMenuItemColors() = MenuDefaults.itemColors(
    textColor = SudokuPalette.TextPrimary,
    leadingIconColor = SudokuPalette.TextSecondary
)

@Composable
fun GameTimerPill(elapsedTimeSeconds: Long, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = SudokuPalette.HomePanel.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, SudokuPalette.GridLine)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                contentDescription = null,
                tint = SudokuPalette.TextAccent,
                modifier = Modifier.size(15.dp)
            )

            Text(
                text = elapsedTimeSeconds.toFormattedTime(),
                style = MaterialTheme.typography.labelLarge,
                color = SudokuPalette.TextAccent,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun String.toTechniqueId(): String {
    return lowercase()
        .replace("-", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString("_")
}

private const val SUDOKU_HAPTIC_DURATION_MILLIS = 18L
private const val SUDOKU_HAPTIC_AMPLITUDE = 36

@Composable
fun GameControls(
    modifier: Modifier = Modifier,
    isNoteMode: Boolean,
    onToggleNoteMode: () -> Unit,
    onUndo: () -> Unit,
    onHintClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onUndo,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = SudokuPalette.ButtonContainer,
                contentColor = SudokuPalette.ButtonContent
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.game_undo),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.game_undo), style = MaterialTheme.typography.labelLarge)
        }

        // notas
        val containerColor =
            if (isNoteMode) SudokuPalette.MentorIndigo else SudokuPalette.ButtonContainer
        val contentColor =
            if (isNoteMode) SudokuPalette.TextOnAccent else SudokuPalette.TextAccent

        Button(
            onClick = onToggleNoteMode,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.game_note_mode),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (isNoteMode) {
                    stringResource(R.string.game_note_mode_on)
                } else {
                    stringResource(R.string.game_note_mode_off)
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onHintClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = SudokuPalette.CellHint,
                contentColor = SudokuPalette.CellHintBorder
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = stringResource(R.string.game_hint),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.game_hint), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF010413
)
@Composable
fun GameScreenPreview(@PreviewParameter(GamePreviewProvider::class) uiState: GameUiState) {
    GameContent(
        uiState = uiState,
        onCellClick = {},
        onNumberInput = {},
        onDeleteInput = {},
        onBackClick = {},
        onToggleNoteMode = {},
        onHintClick = {},
        onGetDebugDumpClick = {},
        onCrashlyticsTestCrashClick = {},
        onDebugFillCandidatesClick = {},
        onDebugPrepareVictoryClick = {},
        onDebugResetPremiumClick = {},
        onSettingsClick = {},
        onOpenTechniquesClick = {},
        onHowToPlayClick = {},
        onOpenTechniqueClick = {},
        currentHintIndex = 0,
        totalHints = uiState.activeHints.size,
        onDismissHint = {},
        onNextHint = {},
        onPrevHint = {},
        onSkipGuidedTutorial = {},
        onNextGuidedTutorialStep = {},
        onVictoryNewGameClick = {},
        onUndo = {}
    )
}
