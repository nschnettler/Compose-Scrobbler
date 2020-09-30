package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.theme.CHIP_CORNER_RADIUS
import de.schnettler.scrobbler.theme.COLOR_ACTIVATED_ALPHA
import de.schnettler.scrobbler.theme.DIVIDER_SIZE
import de.schnettler.scrobbler.theme.PADDING_16
import de.schnettler.scrobbler.theme.PADDING_8

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

@OptIn(ExperimentalLayout::class)
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
                Row(Modifier.preferredHeight(32.dp)) {
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

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    colorSelected: Color = MaterialTheme.colors.secondary.copy(COLOR_ACTIVATED_ALPHA),
    colorNormal: Color = AppColor.BackgroundElevated,
    onSelected: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(CHIP_CORNER_RADIUS),
        color = if (selected) colorSelected else colorNormal
    ) {
        Box(
            gravity = Alignment.Center,
            modifier = Modifier.clickable(onClick = { onSelected() }).preferredHeight(32.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}