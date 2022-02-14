@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package de.schnettler.scrobbler.details.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.details.R

@Composable
fun ExpandingInfoCard(info: String?) {
    if (!info.isNullOrBlank()) {
        OutlinedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        ) {
            ExpandingSummary(info, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun ExpandingSummary(
    text: String?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.clickable(onClick = { expanded = !expanded }, enabled = expandable)) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = text ?: stringResource(id = R.string.bio_unavailable),
                style = textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
                modifier = modifier
            )
        }
    }
}