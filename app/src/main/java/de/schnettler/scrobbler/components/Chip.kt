package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp

@OptIn(ExperimentalLayout::class)
@Composable
fun ChipRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp) {
            items.forEach {
                Chip(text = it, onSelected = { onChipClicked(it) })
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    colorSelected: Color = MaterialTheme.colors.secondary.copy(alpha = 0.4f),
    colorNormal: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    onSelected: () -> Unit) {

    Surface(
        shape = RoundedCornerShape(25.dp),
        color = if (selected) colorSelected else colorNormal
    ) {
        Box(gravity = Alignment.Center,
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