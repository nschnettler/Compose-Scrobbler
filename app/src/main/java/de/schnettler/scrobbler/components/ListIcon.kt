package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.firstLetter

@Composable
fun NameListIcon(title: String) {
    PlainListIconBackground {
        try {
            title.firstLetter()
        } catch (e: NoSuchElementException) {
            title.first().toString()
        }.also {
            Text(text = it)
        }
    }
}

@Composable
fun PlainListIconBackground(
    color: Color = AppColor.BackgroundElevated,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier.preferredSize(40.dp)
    ) {
        Box(gravity = ContentGravity.Center) {
            content()
        }
    }
}