package de.schnettler.scrobbler.components

import androidx.compose.runtime.Composable

@Composable
fun <T> ListWithTitle(title: String, list: List<T>?, content: @Composable (List<T>) -> Unit) {
    if (list?.isNotEmpty() == true) {
        ListTitle(title = title)
        content(list)
    }
}