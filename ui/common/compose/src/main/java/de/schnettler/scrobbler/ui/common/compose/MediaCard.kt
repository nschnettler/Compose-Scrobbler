package de.schnettler.scrobbler.ui.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.util.abbreviate
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun MediaCard(
    name: String,
    modifier: Modifier = Modifier,
    plays: Long = -1,
    imageUrl: String? = null,
    colorCache: DominantColorCache = rememberDominantColorCache(),
    onSelect: () -> Unit,
) {
    var longClicked by remember {
        mutableStateOf(false)
    }

    Card(modifier = modifier) {
        Box(
            modifier = Modifier.clickable(onClick = onSelect, onLongClick = { longClicked = !longClicked }),
            contentAlignment = Alignment.BottomEnd
        ) {
            Providers(AmbientContentAlpha provides ContentAlpha.high) {
                Text(
                    text = name,
                    style = when (name.length) {
                        in 10..20 -> MaterialTheme.typography.h5
                        in 20..Int.MAX_VALUE -> MaterialTheme.typography.h6
                        else -> MaterialTheme.typography.h4
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.Center),
                    maxLines = if (longClicked) 4 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val defaultBackground = AppColor.BackgroundElevated
            val defaultOn = MaterialTheme.colors.onBackground
            var colors by remember {
                mutableStateOf(DominantColors(defaultBackground, defaultOn))
            }

            imageUrl?.let {
                if (!longClicked) {
                    CoilImage(
                        data = it,
                        fadeIn = true,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
                LaunchedEffect(imageUrl) {
                    colors = colorCache.getColorsFromImageUrl(imageUrl)
                }
            }

            if (plays > -1) {
                StatChip(plays = plays, color = colors.color, onColor = colors.onColor)
            }
        }
    }
}

@Composable
private fun StatChip(
    plays: Long,
    color: Color,
    onColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        modifier = Modifier.padding(8.dp),
        contentColor = onColor
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Providers(AmbientContentAlpha provides ContentAlpha.high) {
                    Icon(Icons.Outlined.Hearing.copy(defaultHeight = 16.dp, defaultWidth = 16.dp))
                    Spacer(size = 4.dp, orientation = Orientation.Horizontal)
                    Text(text = plays.abbreviate(), style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}