package de.schnettler.scrobbler.components

import androidx.annotation.ColorRes
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Card
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.CARD_CORNER_RADIUS
import de.schnettler.scrobbler.util.Orientation
import de.schnettler.scrobbler.util.PADDING_4
import de.schnettler.scrobbler.util.PADDING_8
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.firstLetter
import de.schnettler.scrobbler.util.formatter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun <T> Recyclerview(
    items: List<T>,
    height: Dp = 100.dp,
    orientation: Orientation = Orientation.Vertical,
    childView: @Composable() (listing: T) -> Unit
) {
    if (orientation == Orientation.Horizontal) {
        LazyRowItems(items = items, modifier = Modifier.preferredHeight(height)) {
            childView(it)
        }
    } else {
        LazyColumnItems(items = items) {
            childView(it)
        }
    }
}

@Composable
fun <T> GenericHorizontalListingScrollerWithTitle(
    items: List<T>?,
    title: String,
    showIndicator: Boolean = false,
    isLoading: Boolean = false,
    scrollerHeight: Dp,
    childView: @Composable() (listing: T) -> Unit
) {
    when (showIndicator) {
        true -> TitleWithLoadingIndicator(title = title, loading = isLoading)
        false -> TitleComponent(title = title)
    }

    items?.let {
        Recyclerview(
            items = items,
            childView = childView,
            height = scrollerHeight,
            orientation = Orientation.Horizontal
        )
    }
}

@Composable
fun ListingCard(
    name: String,
    plays: Long = -1,
    imageUrl: String? = null,
    height: Dp = 200.dp,
    hintSuffix: String = "Wiedergaben",
    onEntrySelected: () -> Unit
) {

    val titleTextSize = 14.dp
    val subtitleTextsize = if (plays >= 0) 12.dp else 0.dp
    val width = height - 12.dp - titleTextSize - subtitleTextsize

    Column(
        Modifier.preferredSize(width = width, height = height).padding(horizontal = PADDING_8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
            modifier = Modifier.fillMaxSize().padding(bottom = PADDING_8.dp)
        ) {
            Column(modifier = Modifier.clickable(onClick = { onEntrySelected() })) {
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
fun CardBackdrop(width: Dp, imageUrl: String?, placeholderText: String) {
    Box(
        modifier = Modifier.preferredWidth(width).aspectRatio(1F).drawBackground(
            colorResource(id = R.color.colorStroke)
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
                        style = TextStyle(fontSize = width.div(2).value.sp)
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
fun CardContent(
    name: String,
    plays: Long,
    suffix: String,
    titleTextSize: Dp,
    subtitleTextsize: Dp
) {
    // TODO: Replace dp -> sp with sp -> dp logic
    Column(
        modifier = Modifier.padding(
            top = PADDING_4.dp,
            start = PADDING_8.dp,
            end = PADDING_8.dp,
            bottom = PADDING_8.dp
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
                "${formatter.format(plays)} $suffix",
                style = TextStyle(
                    fontSize = subtitleTextsize.value.sp
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TopListScroller(
    title: String,
    state: RefreshableUiState<List<TopListEntryWithData>>,
    height: Dp = 200.dp,
    onEntrySelected: (LastFmEntity) -> Unit
) {
    GenericHorizontalListingScrollerWithTitle(
        items = state.currentData,
        title = title,
        showIndicator = true,
        isLoading = state.isRefreshing,
        scrollerHeight = height
    ) { listing ->
        ListingCard(
            name = listing.data.name,
            plays = listing.topListEntry.count,
            imageUrl = listing.data.imageUrl,
            onEntrySelected = { onEntrySelected(listing.data) },
            height = height
        )
    }
}

@Composable
fun ListingScroller(
    title: String,
    content: List<LastFmStatsEntity>,
    height: Dp,
    playsStyle: PlaysStyle,
    onEntrySelected: (LastFmEntity) -> Unit
) {
    GenericHorizontalListingScrollerWithTitle(
        items = content,
        title = title,
        scrollerHeight = height
    ) { listing ->
        ListingCard(
            name = listing.name,
            plays = when (playsStyle) {
                PlaysStyle.PUBLIC_PLAYS -> listing.plays
                PlaysStyle.USER_PLAYS -> listing.userPlays
                PlaysStyle.NO_PLAYS -> -1
            },
            imageUrl = listing.imageUrl,
            onEntrySelected = { onEntrySelected(listing) },
            height = height
        )
    }
}

@Composable
fun NameListIcon(title: String) {
    PlainListIconBackground {
        try {
            title.firstLetter()
        } catch (e: NoSuchElementException) {
            title.first().toString()
        }.also {
            Text(text = it)
        }
    }
}

@Composable
fun PlainListIconBackground(
    @ColorRes color: Int = R.color.colorBackgroundElevated,
    content: @Composable() () -> Unit
) {
    Surface(
        color = colorResource(id = color),
        shape = CircleShape,
        modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)
    ) {
        Box(gravity = ContentGravity.Center) {
            content()
        }
    }
}