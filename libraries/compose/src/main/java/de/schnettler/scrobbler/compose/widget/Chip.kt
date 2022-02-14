package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.theme.CHIP_CORNER_RADIUS
import de.schnettler.scrobbler.compose.theme.COLOR_ACTIVATED_ALPHA
import de.schnettler.scrobbler.compose.theme.ThemedPreview

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    colorSelected: Color = MaterialTheme.colorScheme.secondary.copy(COLOR_ACTIVATED_ALPHA),
    colorNormal: Color = MaterialTheme.colorScheme.surfaceVariant,
    onSelected: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(CHIP_CORNER_RADIUS),
        color = if (selected) colorSelected else colorNormal
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clickable(onClick = { onSelected() })
                .height(32.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ChipPreview() = ThemedPreview {
    Chip(text = "Content", onSelected = { })
}

@Preview
@Composable
private fun ChipPreviewDark() = ThemedPreview(true) {
    Chip(text = "Content", onSelected = { })
}