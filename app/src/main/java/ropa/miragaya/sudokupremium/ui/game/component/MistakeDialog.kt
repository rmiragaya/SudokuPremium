package ropa.miragaya.sudokupremium.ui.game.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ropa.miragaya.sudokupremium.R
import ropa.miragaya.sudokupremium.ui.component.MentorButton
import ropa.miragaya.sudokupremium.ui.theme.SudokuPalette

@Composable
fun MistakeDialog(mistakeCount: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val mistakeText = pluralStringResource(R.plurals.mistake_dialog_count, mistakeCount, mistakeCount)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SudokuPalette.HomePanel,
            border = BorderStroke(1.dp, SudokuPalette.HomeBorder),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SudokuPalette.TextAccent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, SudokuPalette.TextAccent.copy(alpha = 0.28f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SudokuPalette.TextAccent,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.mistake_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = SudokuPalette.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.mistake_dialog_body, mistakeText),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SudokuPalette.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SudokuPalette.HomeBadgeBackground,
                    border = BorderStroke(1.dp, SudokuPalette.HomeBorder)
                ) {
                    Text(
                        text = stringResource(R.string.mistake_dialog_tip),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SudokuPalette.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                MentorButton(
                    text = stringResource(R.string.mistake_dialog_mark),
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.mistake_dialog_review), color = SudokuPalette.TextSecondary)
                }
            }
        }
    }
}
