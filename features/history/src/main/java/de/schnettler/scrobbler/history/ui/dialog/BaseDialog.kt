package de.schnettler.scrobbler.history.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun NegativeButton(@StringRes textRes: Int, onPressed: () -> Unit) {
    TextButton(
        onClick = onPressed,
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
    ) {
        Text(text = stringResource(id = textRes))
    }
}

@Composable
internal fun PositiveButton(@StringRes textRes: Int, onPressed: () -> Unit) {
    TextButton(
        onClick = onPressed,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary),
    ) {
        Text(text = stringResource(id = textRes))
    }
}