package de.schnettler.scrobbler.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import de.schnettler.scrobbler.compose.theme.AppColor.Blue200
import de.schnettler.scrobbler.compose.theme.AppColor.Blue400
import de.schnettler.scrobbler.compose.theme.AppColor.Jaguar

private val LightThemeColors = lightColorScheme(
    primary = Blue400,
    secondary = Blue400,
//    onPrimary = Color.Black
)

private val DarkThemeColors = darkColorScheme(
    primary = Blue200,
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
        darkTheme -> DarkThemeColors
        else -> LightThemeColors
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
//        shapes = Shapes,
        content = content
    )
}