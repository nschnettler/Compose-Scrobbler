package de.schnettler.scrobbler.components

import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.schnettler.scrobbler.util.Orientation

@Composable
fun Spacer(size: Dp, orientation: Orientation = Orientation.Vertical) {
    when (orientation) {
        Orientation.Vertical -> androidx.compose.foundation.layout.Spacer(modifier = Modifier.preferredHeight(size))
        Orientation.Horizontal -> androidx.compose.foundation.layout.Spacer(modifier = Modifier.preferredWidth(size))
    }
}