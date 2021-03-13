package de.schnettler.scrobbler.ui.search.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.compose.theme.CHIP_CORNER_RADIUS
import de.schnettler.scrobbler.ui.common.compose.theme.DIVIDER_SIZE
import de.schnettler.scrobbler.ui.common.compose.theme.PADDING_16
import de.schnettler.scrobbler.ui.common.compose.widget.Chip
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider

@Composable
fun SelectableChipRow(items: Array<String>, selectedIndex: Int, onSelectionChanged: (Int) -> Unit) {
    Box(
        modifier = Modifier.padding(horizontal = PADDING_16).border(
            width = DIVIDER_SIZE,
            color = AppColor.Divider,
            shape = RoundedCornerShape(CHIP_CORNER_RADIUS)
        )
    ) {
        Row {
            items.forEachIndexed { i, text ->
                Row(Modifier.height(32.dp)) {
                    Chip(text = text, selected = i == selectedIndex, onSelected = {
                        onSelectionChanged(i)
                    }, colorNormal = Color.Transparent)
                    if (i < items.size - 1) {
                        CustomDivider(vertical = true, startIndent = 8.dp)
                    }
                }
            }
        }
    }
}