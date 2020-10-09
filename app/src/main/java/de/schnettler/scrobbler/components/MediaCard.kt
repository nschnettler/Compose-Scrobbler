package de.schnettler.scrobbler.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.Orientation
import de.schnettler.scrobbler.util.abbreviate
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun MediaCard(
    name: String,
    modifier: Modifier = Modifier,
    plays: Long = -1,
    imageUrl: String? = null,
    onSelect: () -> Unit
) {
    Card(modifier = modifier) {
        Box(modifier = Modifier.clickable(onClick = onSelect), alignment = Alignment.BottomEnd) {
            ProvideEmphasis(EmphasisAmbient.current.medium) {
                Text(
                    text = name,
                    style = when (name.length) {
                        in 10..20 -> MaterialTheme.typography.h5
                        in 20..Int.MAX_VALUE -> MaterialTheme.typography.h6
                        else -> MaterialTheme.typography.h4
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            imageUrl?.let {
                CoilImage(
                    data = it,
                    fadeIn = true,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            if (plays > -1) {
                StatChip(plays = plays, onImage = imageUrl != null)
            }
        }
    }
}

@Composable
private fun StatChip(
    plays: Long,
    onImage: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (onImage) Color.Black.copy(0.3F) else AppColor.BackgroundElevated,
        modifier = Modifier.padding(8.dp),
        contentColor = if (onImage) Color.White else MaterialTheme.colors.onBackground
    ) {
        Box(
            alignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProvideEmphasis(EmphasisAmbient.current.medium) {
                    Icon(asset = Icons.Outlined.Hearing.copy(defaultHeight = 16.dp, defaultWidth = 16.dp))
                    Spacer(size = 4.dp, orientation = Orientation.Horizontal)
                    Text(text = plays.abbreviate(), style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}