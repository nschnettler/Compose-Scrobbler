package de.schnettler.scrobbler.util

import androidx.ui.graphics.Color
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

const val AUTH_ENDPOINT = "https://www.last.fm/api/auth/"
const val REDIRECT_URL = "de.schnettler.scrobble://auth"

const val PADDING_16 = 16
const val PADDING_8 = 8
const val PADDING_4 = 4
const val DIVIDER_SIZE = 1
const val CHIP_CORNER_RADIUS = 16
const val COLOR_ACTIVATED_ALPHA = 0.4f
const val COLOR_NORMAL_ALPHA = 0.12f
const val CARD_CORNER_RADIUS = 12

val lightThemeColors = lightColorPalette(
    primary = Color(0xFF7E8ACD),
    primaryVariant = Color(0xFF7E8ACD),
    secondary = Color(0xFF7E8ACD),
    secondaryVariant = Color(0x7E8ACD),
    onPrimary = Color.Black
)
val darkThemeColors = darkColorPalette(
    primary = Color(0xFF7E8ACD),
    primaryVariant = Color(0xFF7E8ACD),
    secondary = Color(0xFF7E8ACD),
    background = Color(0xFF202030),
    surface = Color(0xFF202030)
)
