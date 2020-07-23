package de.schnettler.scrobbler.components

import androidx.annotation.ColorRes
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
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
import de.schnettler.scrobbler.util.LoadingState
import de.schnettler.scrobbler.screens.formatter
import de.schnettler.scrobbler.util.Orientation
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.firstLetter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

enum class PlaysStyle() {
    USER_PLAYS,
    PUBLIC_PLAYS,
    NO_PLAYS
}

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
    when(showIndicator) {
        true -> TitleWithLoadingIndicator(title = title, loading = isLoading)
        false -> TitleComponent(title = title)
    }

    items?.let {
        Recyclerview(items = items, childView = childView, height = scrollerHeight, orientation = Orientation.Horizontal)
    }
}

@Composable
fun ListingCard(
    data: LastFmEntity,
    height: Dp = 200.dp,
    plays: Long = -1,
    hintSuffix: String = "Wiedergaben",
    onEntrySelected: (LastFmEntity) -> Unit) {

    val titleTextSize = 14.dp
    val subtitleTextsize = if (plays >= 0) 12.dp else 0.dp
    val width = height - 12.dp - titleTextSize - subtitleTextsize

    Column(Modifier.preferredSize(width = width, height = height).padding(horizontal = 8.dp)) {
        Card(
            shape = RoundedCornerShape(cardCornerRadius),
            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)
        ) {
            Column(modifier = Modifier.clickable(onClick = { onEntrySelected.invoke(data) })) {
                Box(modifier = Modifier.preferredWidth(width).aspectRatio(1F).drawBackground(
                    colorResource(id = R.color.colorStroke))) {
                    when(val imageUrl = data.imageUrl) {
                        null -> {
                            Box(gravity = ContentGravity.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = data.name.firstLetter(), style = TextStyle(fontSize = width.div(2).value.sp))
                            }
                        }
                        else -> CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                }
                //TODO: Replace dp -> sp with sp -> dp logic
                Column(modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                    Text(data.name,
                        style = TextStyle(
                            fontSize = titleTextSize.value.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if(plays >= 0) {
                        Text(
                            "${formatter.format(plays)} $hintSuffix",
                            style = TextStyle(
                                fontSize = subtitleTextsize.value.sp
                            ), maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopListScroller(
    title: String,
    content: LoadingState<List<TopListEntryWithData>>,
    height: Dp = 200.dp,
    onEntrySelected: (LastFmEntity) -> Unit) {
    GenericHorizontalListingScrollerWithTitle(
        items = content.data,
        title = title,
        showIndicator = true,
        scrollerHeight = height,
        isLoading = content is LoadingState.Loading
    ) { listing ->
        ListingCard(
            data = listing.data, 
            onEntrySelected = onEntrySelected, 
            height = height, 
            plays = listing.topListEntry.count
        )
    }
}

@Composable
fun ListingScroller(
    title: String,
    content: List<LastFmStatsEntity>,
    height: Dp,
    playsStyle: PlaysStyle,
    onEntrySelected: (LastFmEntity) -> Unit) {

    GenericHorizontalListingScrollerWithTitle(
        items = content,
        title = title,
        scrollerHeight = height
    ) { listing ->
        ListingCard(
            data = listing,
            onEntrySelected = onEntrySelected,
            height = height,
            plays = when(playsStyle) {
                PlaysStyle.PUBLIC_PLAYS -> listing.plays
                PlaysStyle.USER_PLAYS -> listing.userPlays
                PlaysStyle.NO_PLAYS -> -1
            }
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
fun PlainListIconBackground(@ColorRes color: Int = R.color.colorBackgroundElevated, content: @Composable() () -> Unit) {
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