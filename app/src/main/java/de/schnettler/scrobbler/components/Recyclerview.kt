package de.schnettler.scrobbler.components

import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.BaseEntity
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.util.Orientation
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.RefreshableUiState

@Composable
fun <T> Recyclerview(
    items: List<T>,
    height: Dp = 100.dp,
    orientation: Orientation = Orientation.Vertical,
    childView: @Composable (listing: T) -> Unit
) {
    if (orientation == Orientation.Horizontal) {
        LazyRowFor(items = items, modifier = Modifier.preferredHeight(height)) {
            childView(it)
        }
    } else {
        LazyColumnFor(items = items) {
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
    childView: @Composable (listing: T) -> Unit
) {
    when (showIndicator) {
        true -> TitleWithLoadingIndicator(title = title, loading = isLoading)
        false -> ListTitle(title = title)
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
fun TopListScroller(
    title: String,
    state: RefreshableUiState<List<Toplist>>,
    height: Dp = 200.dp,
    actionHandler: (UIAction) -> Unit
) {
    GenericHorizontalListingScrollerWithTitle(
        items = state.currentData,
        title = title,
        showIndicator = true,
        isLoading = state.isRefreshing,
        scrollerHeight = height
    ) { listing ->
        MediaCard(
            name = listing.value.name,
            plays = listing.listing.count,
            imageUrl = listing.value.imageUrl,
            onEntrySelected = { actionHandler(ListingSelected(listing.value)) },
            height = height
        )
    }
}

@Composable
fun ListingScroller(
    title: String,
    content: List<BaseEntity>,
    height: Dp,
    playsStyle: PlaysStyle,
    actionHandler: (UIAction) -> Unit
) {
    GenericHorizontalListingScrollerWithTitle(
        items = content,
        title = title,
        scrollerHeight = height
    ) { listing ->
        when (listing) {
            is EntityWithStats -> {
                MediaCard(
                    name = listing.entity.name,
                    plays = when (playsStyle) {
                        PlaysStyle.PUBLIC_PLAYS -> listing.stats.plays
                        PlaysStyle.USER_PLAYS -> listing.stats.userPlays
                        else -> -1
                    },
                    imageUrl = listing.entity.imageUrl,
                    onEntrySelected = { actionHandler(ListingSelected(listing.entity)) },
                    height = height
                )
            }
            is LastFmEntity -> {
                MediaCard(
                    name = listing.name,
                    plays = -1,
                    imageUrl = listing.imageUrl,
                    onEntrySelected = { actionHandler(ListingSelected(listing)) },
                    height = height
                )
            }
        }
    }
}