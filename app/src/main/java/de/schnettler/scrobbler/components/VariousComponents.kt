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
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import androidx.ui.unit.sp

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
                modifier = Modifier.size(24.dp) + Modifier.gravity(Alignment.CenterVertically))
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