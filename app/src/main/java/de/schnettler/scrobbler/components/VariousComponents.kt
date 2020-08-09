package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleComponent(title: String) {
    Text(title, style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.W900,
        fontSize = 14.sp), modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun TitleWithLoadingIndicator(title: String, loading: Boolean) {
    Row {
        TitleComponent(title = title)
        if (loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp).gravity(Alignment.CenterVertically))
        }
    }
}

@Composable
fun ExpandingSummary(
    text: String?,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    var expanded by state { false }
    Box(modifier = Modifier.clickable(onClick = { expanded = !expanded }, enabled = expandable)) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                text = text ?: "No Bio available",
                style = textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
                modifier = modifier)
        }
    }
}