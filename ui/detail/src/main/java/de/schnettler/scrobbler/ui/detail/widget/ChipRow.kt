package de.schnettler.scrobbler.ui.detail.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.schnettler.scrobbler.ui.common.compose.widget.Chip
import de.schnettler.scrobbler.ui.common.compose.theme.PADDING_16
import de.schnettler.scrobbler.ui.common.compose.theme.PADDING_8

@OptIn(ExperimentalLayout::class)
@Composable
fun ChipRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    Box(modifier = Modifier.padding(horizontal = PADDING_16)) {
        FlowRow(mainAxisSpacing = PADDING_8, crossAxisSpacing = PADDING_16) {
            items.forEach {
                Chip(text = it, onSelected = { onChipClicked(it) })
            }
        }
    }
}