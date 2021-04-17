package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.compose.theme.CHIP_CORNER_RADIUS
import de.schnettler.scrobbler.ui.common.compose.theme.COLOR_ACTIVATED_ALPHA
import de.schnettler.scrobbler.ui.common.compose.util.ThemedPreview

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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clickable(onClick = { onSelected() })
                .height(32.dp)
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

@Preview
@Composable
private fun ChipPreview() = ThemedPreview {
    Chip(text = "Content", onSelected = { })
}