package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.schnettler.scrobbler.theme.PADDING_4
import de.schnettler.scrobbler.theme.PADDING_8
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.util.firstLetter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun MediaCard(
    name: String,
    plays: Long = -1,
    imageUrl: String? = null,
    height: Dp = 200.dp,
    hintSuffix: String = "Wiedergaben",
    onSelect: () -> Unit
) {

    val titleTextSize = 14.dp
    val subtitleTextsize = if (plays >= 0) 12.dp else 0.dp
    val width = height - 12.dp - titleTextSize - subtitleTextsize

    Column(
        Modifier.preferredSize(width = width, height = height).padding(horizontal = PADDING_8)
    ) {
        Card(modifier = Modifier.fillMaxSize().padding(bottom = PADDING_8)) {
            Column(modifier = Modifier.clickable(onClick = { onSelect() })) {
                CardBackdrop(width = width, imageUrl = imageUrl, placeholderText = name)
                CardContent(
                    name = name,
                    plays = plays,
                    suffix = hintSuffix,
                    titleTextSize = titleTextSize,
                    subtitleTextsize = subtitleTextsize
                )
            }
        }
    }
}

@Composable
private fun CardBackdrop(width: Dp, imageUrl: String?, placeholderText: String) {
    Box(
        modifier = Modifier.preferredWidth(width).aspectRatio(1F).background(
            MaterialTheme.colors.onSurface.copy(0.05F)
        )
    ) {
        when (imageUrl) {
            null -> {
                Box(
                    gravity = ContentGravity.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = placeholderText.firstLetter(),
                        style = TextStyle(fontSize = width.div(2).value.sp, color = contentColor().copy(alpha = 0.7F))
                    )
                }
            }
            else -> CoilImageWithCrossfade(
                data = imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CardContent(
    name: String,
    plays: Long,
    suffix: String,
    titleTextSize: Dp,
    subtitleTextsize: Dp
) {
    // TODO: Replace dp -> sp with sp -> dp logic
    Column(
        modifier = Modifier.padding(
            top = PADDING_4,
            start = PADDING_8,
            end = PADDING_8,
            bottom = PADDING_8
        )
    ) {
        Text(
            name,
            style = TextStyle(
                fontSize = titleTextSize.value.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (plays >= 0) {
            Text(
                "${plays.abbreviate()} $suffix",
                style = TextStyle(
                    fontSize = subtitleTextsize.value.sp
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}