package de.schnettler.scrobbler.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import de.schnettler.scrobbler.compose.theme.AppColor.Blue200
import de.schnettler.scrobbler.compose.theme.AppColor.Blue400
import de.schnettler.scrobbler.compose.theme.AppColor.Jaguar
import androidx.compose.material.MaterialTheme as LegacyMaterialTheme

private val LightThemeColorScheme = lightColorScheme(
    primary = Blue400,
    secondary = Blue400,
)

private val DarkThemeColorScheme = darkColorScheme(
    primary = Blue200,
    secondary = Blue200,
    background = Jaguar,
    surface = Jaguar
)

private val LightThemeColors = lightColors(
    primary = Blue400,
    primaryVariant = Blue400,
    secondary = Blue400,
    secondaryVariant = Blue400,
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
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkThemeColorScheme
        else -> LightThemeColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun LegacyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    LegacyMaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        shapes = Shapes,
        content = content
    )
}