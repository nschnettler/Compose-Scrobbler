package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.theme.AppColor

@Composable
fun CustomDivider(
    modifier: Modifier = Modifier,
    color: Color = AppColor.Divider,
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
        Box(modifier.then(indentMod).fillMaxHeight().preferredWidth(thickness).background(color))
    } else {
        Box(modifier.then(indentMod).fillMaxWidth().preferredHeight(thickness).background(color))
    }
}