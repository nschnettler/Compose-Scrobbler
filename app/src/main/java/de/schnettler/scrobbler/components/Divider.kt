package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    vertical: Boolean = false
) {
    val indentMod = if (startIndent.value != 0f) {
        if (vertical) Modifier.padding(vertical = startIndent) else Modifier.padding(start = startIndent)
    } else {
        Modifier
    }
    if (vertical) {
        Box(modifier.plus(indentMod).fillMaxHeight().preferredWidth(thickness).drawBackground(color))
    } else {
        Box(modifier.plus(indentMod).fillMaxWidth().preferredHeight(thickness).drawBackground(color))
    }

}

private const val DividerAlpha = 0.12f