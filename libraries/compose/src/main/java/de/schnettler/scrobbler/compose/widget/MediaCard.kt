@file:OptIn(ExperimentalMaterial3Api::class)

package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.theme.DominantColorState
import de.schnettler.scrobbler.compose.theme.DominantColors
import de.schnettler.scrobbler.compose.theme.ThemedPreview
import de.schnettler.scrobbler.compose.theme.rememberDominantColorState
import de.schnettler.scrobbler.core.ktx.abbreviate

@Composable
fun MediaCard(
    name: String,
    modifier: Modifier = Modifier,
    plays: Long = -1,
    imageUrl: String? = null,
    colorState: DominantColorState = rememberDominantColorState(),
    onSelect: () -> Unit,
) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier.clickable(onClick = onSelect).fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = name,
                    style = when (name.length) {
                        in 10..20 -> MaterialTheme.typography.headlineMedium
                        in 20..Int.MAX_VALUE -> MaterialTheme.typography.headlineSmall
                        else -> MaterialTheme.typography.headlineLarge
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val defaultBackground = AppColor.BackgroundElevated
            val defaultOn = MaterialTheme.colorScheme.onBackground
            var colors by remember {
                mutableStateOf(DominantColors(defaultBackground, defaultOn))
            }

            imageUrl?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(it).crossfade(true).build(),
                    contentDescription = "Picture of $name",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                LaunchedEffect(imageUrl) {
                    colors = colorState.getColorsFromImageUrl(imageUrl)
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
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(name) {
                        width = Dimension.preferredWrapContent
                        end.linkTo(spacer.start)
                        start.linkTo(parent.start)
                    }
                )

                Text(text = " • ",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.constrainAs(spacer) {
                        width = Dimension.wrapContent
                        end.linkTo(stat.start)
                    }
                )

                Text(
                    text = plays.abbreviate(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.constrainAs(stat) {
                        width = Dimension.wrapContent
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}

// Preview

@Preview
@Composable
private fun MediaCardPreviewLight() = ThemedPreview {
    MediaCardPreviewContent()
}

@Preview
@Composable
private fun MediaCardPreviewDark() = ThemedPreview(true) {
    MediaCardPreviewContent()
}

@Composable
private fun MediaCardPreviewContent() {
    Box(Modifier.padding(8.dp)) {
        MediaCard("Name", Modifier.size(160.dp), plays = 10L, onSelect = { })
    }
}