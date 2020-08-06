package de.schnettler.scrobbler.theme

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import de.schnettler.scrobbler.theme.AppColor.Blue200
import de.schnettler.scrobbler.theme.AppColor.Blue400
import de.schnettler.scrobbler.theme.AppColor.Jaguar

private val LightThemeColors = lightColorPalette(
    primary = Blue400,
    primaryVariant = Blue400,
    secondary = Blue400,
    secondaryVariant = Blue400,
    onPrimary = Color.Black
)

private val DarkThemeColors = darkColorPalette(
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