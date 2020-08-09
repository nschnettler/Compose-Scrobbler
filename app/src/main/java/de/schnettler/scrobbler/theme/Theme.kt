package de.schnettler.scrobbler.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.schnettler.scrobbler.theme.AppColor.Blue200
import de.schnettler.scrobbler.theme.AppColor.Blue400
import de.schnettler.scrobbler.theme.AppColor.Jaguar

private val LightThemeColors = lightColors(
    primary = Blue400,
    primaryVariant = Blue400,
    secondary = Blue400,
    secondaryVariant = Blue400,
    onPrimary = Color.Black
)

private val DarkThemeColors = darkColors(
    primary = Blue200,
    primaryVariant = Blue200,
    secondary = Blue200,
    background = Jaguar,
    surface = Jaguar
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        shapes = Shapes,
        content = content
    )
}