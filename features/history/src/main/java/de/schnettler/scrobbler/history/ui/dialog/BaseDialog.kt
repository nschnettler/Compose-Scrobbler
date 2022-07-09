package de.schnettler.scrobbler.history.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun NegativeButton(@StringRes textRes: Int, onPressed: () -> Unit) {
    TextButton(
        onClick = onPressed,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)),
    ) {
        Text(text = stringResource(id = textRes))
    }
}

@Composable
internal fun PositiveButton(@StringRes textRes: Int, onPressed: () -> Unit) {
    TextButton(
        onClick = onPressed,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
    ) {
        Text(text = stringResource(id = textRes))
    }
}