package de.schnettler.scrobbler.ui.detail.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.widget.Chip

@Composable
fun ChipRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) {
            Chip(text = it, onSelected = { onChipClicked(it) })
        }
    }
}

@Preview
@Composable
private fun ChipRowPreview() {
    ChipRow(listOf("Chip 1", "Chip 2", "Chip 3"))
}