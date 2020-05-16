package de.schnettler.scrobbler.components

import androidx.compose.*
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.ripple
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import androidx.ui.unit.sp

@Composable
fun TitleComponent(title: String) {
    Text(title, style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.W900,
        fontSize = 14.sp), modifier = Modifier.padding(16.dp) +
            Modifier.fillMaxWidth()
    )
}

@Composable
fun ExpandingSummary(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    var canTextExpand by stateFor(text) { true }

    Box(modifier = Modifier.ripple(bounded = true, enabled = expandable && canTextExpand)) {
        var expanded by state { false }

        Clickable(onClick = { expanded = !expanded }, enabled = expandable && canTextExpand) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                Text(
                    text = text,
                    style = textStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
                    modifier = modifier)
            }
        }
    }
}