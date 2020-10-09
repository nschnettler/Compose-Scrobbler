package de.schnettler.scrobbler.components

import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.util.Orientation

@Composable
fun <T> Recyclerview(
    items: List<T>,
    height: Dp = 100.dp,
    orientation: Orientation = Orientation.Vertical,
    modifier: Modifier = Modifier,
    childView: @Composable (listing: T) -> Unit
) {
    if (orientation == Orientation.Horizontal) {
        LazyRowFor(items = items, modifier = modifier.preferredHeight(height)) {
            childView(it)
        }
    } else {
        LazyColumnFor(items = items, modifier) {
            childView(it)
        }
    }
}