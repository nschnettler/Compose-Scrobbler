package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import de.schnettler.scrobbler.compose.theme.ThemedPreview

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

@Composable
fun ChipFlowRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp, modifier = Modifier.padding(horizontal = 16.dp)) {
        items.forEach {
            Chip(text = it, onSelected = { onChipClicked(it) })
        }
    }
}

@Preview
@Composable
private fun ChipRowPreview() = ThemedPreview {
    ChipRow(listOf("Chip 1", "Chip 2", "Chip 3"))
}

@Preview
@Composable
private fun ChipFlowRowPreview() = ThemedPreview {
    Box(modifier = Modifier.width(200.dp)) {
        ChipFlowRow(listOf("Chip 1", "Chip 2", "Chip 3"))
    }
}