package de.schnettler.scrobbler.components

import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import de.schnettler.scrobbler.R

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
                color = colorResource(id = R.color.colorAccent),
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
    val canTextExpand by stateFor(text) { true }

    Box(modifier = Modifier.ripple(bounded = true, enabled = expandable && canTextExpand)) {
        var expanded by state { false }

        Clickable(onClick = { expanded = !expanded }, enabled = expandable && canTextExpand) {
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
}