package de.schnettler.scrobbler.ui.common.compose.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColor {
    val Blue400 = Color(0xFF7E8ACD)
    val Blue200 = Color(0xFFA4B0D9)
    val Jaguar = Color(0xFF202030)
    @Composable
    val Divider: Color
        get() = BackgroundElevated
    @Composable val BackgroundElevated: Color
        get() = MaterialTheme.colors.onBackground.copy(0.05F)
}
