package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Spacer as ComposeSpacer

@Composable
fun Spacer(size: Dp, orientation: Orientation = Orientation.Vertical) {
    when (orientation) {
        Orientation.Vertical -> ComposeSpacer(modifier = Modifier.height(size))
        Orientation.Horizontal -> ComposeSpacer(modifier = Modifier.width(size))
    }
}

enum class Orientation {
    Vertical,
    Horizontal
}