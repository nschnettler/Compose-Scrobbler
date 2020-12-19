package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Spacer(size: Dp, orientation: Orientation = Orientation.Vertical) {
    when (orientation) {
        Orientation.Vertical -> androidx.compose.foundation.layout.Spacer(modifier = Modifier.preferredHeight(size))
        Orientation.Horizontal -> androidx.compose.foundation.layout.Spacer(modifier = Modifier.preferredWidth(size))
    }
}

enum class Orientation {
    Vertical,
    Horizontal
}