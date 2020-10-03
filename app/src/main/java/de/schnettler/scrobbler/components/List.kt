package de.schnettler.scrobbler.components

import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.runtime.Composable
import de.schnettler.scrobbler.util.Orientation

@Composable
fun <T> ListWithTitle(title: String, list: List<T>?, content: @Composable (List<T>) -> Unit) {
    if (list?.isNotEmpty() == true) {
        ListTitle(title = title)
        content(list)
    }
}

@Composable
fun <T> LazyListWithTitle(title: String, data: List<T>?, orientation: Orientation = Orientation.Horizontal, block: @Composable (T) -> Unit) {
    if (data?.isNotEmpty() == true) {
        ListTitle(title = title)
        when(orientation) {
            Orientation.Horizontal -> LazyRowFor(items = data) { item ->
                block(item)
            }
            Orientation.Vertical -> LazyColumnFor(items = data) { item ->
                block(item)
            }
        }
    }
}