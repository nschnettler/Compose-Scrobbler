package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.coil.CoilImage
import de.schnettler.scrobbler.ui.common.compose.DominantColorCache
import de.schnettler.scrobbler.ui.common.compose.DominantColors
import de.schnettler.scrobbler.ui.common.compose.rememberDominantColorCache
import de.schnettler.scrobbler.ui.common.compose.theme.AppColor
import de.schnettler.scrobbler.ui.common.util.abbreviate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCard(
    name: String,
    modifier: Modifier = Modifier,
    plays: Long = -1,
    imageUrl: String? = null,
    colorCache: DominantColorCache = rememberDominantColorCache(),
    onSelect: () -> Unit,
) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier.clickable(onClick = onSelect),
            contentAlignment = Alignment.BottomEnd
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = name,
                    style = when (name.length) {
                        in 10..20 -> MaterialTheme.typography.h5
                        in 20..Int.MAX_VALUE -> MaterialTheme.typography.h6
                        else -> MaterialTheme.typography.h4
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val defaultBackground = AppColor.BackgroundElevated
            val defaultOn = MaterialTheme.colors.onBackground
            var colors by remember {
                mutableStateOf(DominantColors(defaultBackground, defaultOn))
            }

            imageUrl?.let {
                CoilImage(
                    data = it,
                    fadeIn = true,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )
                LaunchedEffect(imageUrl) {
                    colors = colorCache.getColorsFromImageUrl(imageUrl)
                }
            }

            if (plays > -1) {
                InfoChip(text = name, plays = plays, color = colors.color, onColor = colors.onColor)
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
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
            ConstraintLayout {
                val (name, stat, spacer) = createRefs()
                Text(
                    text = text,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(name) {
                        width = Dimension.preferredWrapContent
                        end.linkTo(spacer.start)
                        start.linkTo(parent.start)
                    }
                )

                Text(text = " • ",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(spacer) {
                        width = Dimension.wrapContent
                        end.linkTo(stat.start)
                    }
                )

                Text(
                    text = plays.abbreviate(),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(stat) {
                        width = Dimension.wrapContent
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}