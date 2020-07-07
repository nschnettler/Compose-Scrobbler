package de.schnettler.scrobbler.components

import androidx.annotation.ColorRes
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.LoadingState
import de.schnettler.scrobbler.screens.formatter
import de.schnettler.scrobbler.screens.preview.FakeTopListEntry
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.firstLetter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

enum class PlaysStyle() {
    USER_PLAYS,
    PUBLIC_PLAYS,
    NO_PLAYS
}

@Composable
fun <T>GenericHorizontalListingScroller(
    items: List<T>,
    height: Dp,
    childView: @Composable() (listing: T) -> Unit) {
//    LazyRowItems(items = items, modifier = Modifier.preferredHeight(height)) {
//        childView(it)
//    }
    LazyRowItems(items = items, modifier = Modifier.preferredHeightIn(0.dp, 225.dp)) {
        childView(it)
    }
    Modifier.fillMaxWidth()
}

@Composable
fun <T> GenericHorizontalListingScrollerWithTitle(
    items: List<T>?,
    title: String,
    showIndicator: Boolean = false,
    isLoading: Boolean = false,
    itemHeight: Dp,
    childView: @Composable() (listing: T) -> Unit
) {
    when(showIndicator) {
        true -> TitleWithLoadingIndicator(title = title, loading = isLoading)
        false -> TitleComponent(title = title)
    }

    items?.let {
        GenericHorizontalListingScroller<T>(items = items, childView = childView, height = itemHeight)
    }
}

@Composable
fun ListingCard(
    data: ListingMin,
    width: Dp = 136.dp,
    height: Dp = 136.dp,
    plays: Long = -1,
    hintTextSize: TextUnit = 62.sp,
    hintSuffix: String = "Wiedergaben",
    onEntrySelected: (ListingMin) -> Unit) {
    Column(modifier = Modifier.preferredWidth(width) + Modifier.padding(horizontal = 8.dp)) {
        Card(shape = RoundedCornerShape(cardCornerRadius),
            modifier = Modifier.preferredWidth(width) + Modifier.padding(bottom = 8.dp)
        ) {
            Column(modifier = Modifier.clickable(
                onClick = { onEntrySelected.invoke(data) })) {
                Box(modifier = Modifier.preferredHeight(height - 8.dp)) {
                    when (val imageUrl = data.imageUrl) {
                        null -> {
                            Box(gravity = ContentGravity.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = data.name.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                            }
                        }
                        else -> {
                            CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                Column(modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                    Text(data.name,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if(plays >= 0) {
                        Text(
                            "${formatter.format(plays)} $hintSuffix",
                            style = TextStyle(
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NewListingCard(
    @PreviewParameter(FakeTopListEntry::class) data: ListingMin,
    plays: Long = -1,
    hintTextSize: TextUnit = 62.sp,
    hintSuffix: String = "Wiedergaben",
    onEntrySelected: (ListingMin) -> Unit = {},
    height: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(cardCornerRadius),
        modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp).padding(bottom = 8.dp)
    ) {
        Column(Modifier.fillMaxHeight().clickable(onClick = { onEntrySelected.invoke(data) })) {
            //Image
            Box(modifier = Modifier.weight(1F).aspectRatio(1F)) {
                when(val imageUrl = data.imageUrl) {
                    null -> {
                        Box(gravity = ContentGravity.Center, modifier = Modifier.fillMaxSize()) {
                            Text(text = data.name.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                        }
                    }
                    else -> CoilImageWithCrossfade(data = imageUrl, modifier = Modifier.fillMaxSize())
                }
            }
            //TODO: Find good method to set text maxwidth to width of artwork
            Column(modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp).preferredWidthIn(0.dp, height.times(0.6F))) {
                Text(data.name,
                    style = TextStyle(
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if(plays >= 0) {
                    Text(
                        "${formatter.format(plays)} $hintSuffix",
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TopListScroller(
        title: String,
        content: LoadingState<List<TopListEntryWithData>>,
        onEntrySelected: (ListingMin) -> Unit) {

    val height = 200.dp
    GenericHorizontalListingScrollerWithTitle(
        items = content.data,
        title = title,
        showIndicator = true,
        itemHeight = height,
        isLoading = content is LoadingState.Loading
    ) { listing ->
        ListingCard(
            data = listing.data,
            onEntrySelected = onEntrySelected,
            width = 172.dp,
            height = 172.dp,
            plays = listing.topListEntry.count
        )
//        NewListingCard(
//                data = listing.data,
//        onEntrySelected = onEntrySelected,
//        plays = listing.topListEntry.count
//        )
    }
}

@Composable
fun ListingScroller(
    title: String,
    content: List<ListingMin>,
    width: Dp,
    height: Dp,
    hintTextSize: TextUnit = 62.sp,
    playsStyle: PlaysStyle,
    onEntrySelected: (ListingMin) -> Unit) {

    GenericHorizontalListingScrollerWithTitle(
        items = content,
        title = title,
        itemHeight = 200.dp
    ) { listing ->
        ListingCard(
            data = listing,
            onEntrySelected = onEntrySelected,
            width = width,
            height = height,
            hintTextSize = hintTextSize,
            plays = when(playsStyle) {
                PlaysStyle.PUBLIC_PLAYS -> listing.plays
                PlaysStyle.USER_PLAYS -> listing.userPlays
                PlaysStyle.NO_PLAYS -> -1
            }
        )
    }
}

@Composable
fun NameListIcon(item: ListingMin) {
    PlainListIconBackground {
        Text(text = item.name.firstLetter())
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