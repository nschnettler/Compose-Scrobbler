package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.scrobbler.model.LoadingState2
import de.schnettler.scrobbler.screens.formatter
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
    childView: @Composable() (listing: T) -> Unit) {
    HorizontalScroller(modifier = Modifier.fillMaxWidth()) {
        Row {
            items.forEach {
                childView(it)
            }
        }
    }
}

@Composable
fun <T> GenericHorizontalListingScrollerWithTitle(
    items: List<T>?,
    title: String,
    showIndicator: Boolean = false,
    isLoading: Boolean = false,
    childView: @Composable() (listing: T) -> Unit
) {
    when(showIndicator) {
        true -> TitleWithLoadingIndicator(title = title, loading = isLoading)
        false -> TitleComponent(title = title)
    }

    items?.let {
        GenericHorizontalListingScroller<T>(items = items, childView = childView)
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
            modifier = Modifier.preferredWidth(width) + Modifier.clickable(
                onClick = { onEntrySelected.invoke(data) }) + Modifier.padding(bottom = 8.dp)
        ) {
            Column() {
                Box(modifier = Modifier.preferredHeight(height - 8.dp)) {
                    when (val imageUrl = data.imageUrl) {
                        null -> {
                            Box(gravity = ContentGravity.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = data.name.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                            }
                        }
                        else -> {
                            CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop)
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

@Composable
fun TopListScroller(
    title: String,
    content: LoadingState2<List<TopListEntryWithData>>,
    onEntrySelected: (ListingMin) -> Unit) {

    GenericHorizontalListingScrollerWithTitle(
        items = content.data,
        title = title,
        showIndicator = true,
        isLoading = content is LoadingState2.Loading
    ) { listing ->
        ListingCard(
            data = listing.data,
            onEntrySelected = onEntrySelected,
            width = 172.dp,
            height = 172.dp,
            plays = listing.topListEntry.count
        )
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
        title = title
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