package de.schnettler.scrobbler.ui.common.compose.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import de.schnettler.scrobbler.ui.common.compose.theme.AppTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}