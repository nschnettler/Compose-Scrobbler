package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import androidx.ui.unit.sp

@Composable
fun TitleComponent(title: String) {
    Text(title, style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.W900,
        fontSize = 14.sp, color = Color.Black), modifier = Modifier.padding(16.dp) +
            Modifier.fillMaxWidth()
    )
}