package de.schnettler.scrobbler.compose.theme

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

@Composable
fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}