package de.schnettler.scrobbler.ui.charts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider
import de.schnettler.scrobbler.ui.common.compose.widget.FullScreenLoading
import de.schnettler.scrobbler.ui.common.compose.widget.IndexListIconBackground
import de.schnettler.scrobbler.ui.common.compose.widget.LoadingContent
import de.schnettler.scrobbler.ui.common.compose.widget.Pager
import de.schnettler.scrobbler.ui.common.compose.widget.PagerState
import de.schnettler.scrobbler.ui.common.util.abbreviate
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import timber.log.Timber

@Composable
fun ChartScreen(
    viewModel: ChartViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = viewModel.pagerState

    val selectedTab = ChartTab.values()[pagerState.currentPage]

    Column {
        Spacer(modifier = Modifier.statusBarsHeight())
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.surface
        ) {
            ChartTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { pagerState.currentPage = tab.ordinal },
                    text = { Text(text = stringResource(id = tab.text)) }
                )
            }
        }
        ChartPager(
            viewModel = viewModel,
            items = ChartTab.values(),
            pagerState = pagerState,
            actionHandler = actionHandler,
            modifier = modifier,
        )
    }
}

@Composable
private fun ChartList(
    chartData: LazyPagingItems<Toplist>,
    handler: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) = LazyColumn(modifier) {
    itemsIndexed(chartData) { index, entry ->
        when (entry) {
            is TopListArtist -> ChartArtistListItem(
                name = entry.value.name,
                listener = entry.listing.count,
                index = index,
                onClicked = { handler(ListingSelected(entry.value)) }
            )
            is TopListTrack -> ChartTrackListItem(
                name = entry.value.name,
                artist = entry.value.artist,
                index = index,
                onClicked = { handler(ListingSelected(entry.value)) }
            )
        }
        CustomDivider()
    }

    val loadState = chartData.loadState
    when {
        loadState.append is LoadState.Loading -> {
            item { LoadingItem() }
        }
        loadState.refresh is LoadState.Error -> {
//            val e = lazyMovieItems.loadState.refresh as LoadState.Error
//            item {
//                ErrorItem(
//                    message = e.error.localizedMessage!!,
//                    modifier = Modifier.fillParentMaxSize(),
//                    onClickRetry = { retry() }
//                )
//            }
        }
        loadState.append is LoadState.Error -> {
//            val e = lazyMovieItems.loadState.append as LoadState.Error
//            item {
//                ErrorItem(
//                    message = e.error.localizedMessage!!,
//                    onClickRetry = { retry() }
//                )
//            }
        }
    }
}

@Composable
fun LoadingItem() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ChartPager(
    viewModel: ChartViewModel,
    items: Array<ChartTab>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = remember { PagerState() },
    actionHandler: (UIAction) -> Unit
) {
    pagerState.maxPage = (items.size - 1).coerceAtLeast(0)

    Pager(
        state = pagerState,
        modifier = modifier
    ) {
        val pagingItems = when (items[page]) {
            ChartTab.Artist -> viewModel.artistCharts.collectAsLazyPagingItems()
            ChartTab.Track -> viewModel.trackCharts.collectAsLazyPagingItems()
        }
        val loadStates = pagingItems.loadState

        if (loadStates.refresh is LoadState.Error) {
            val errorState = loadStates.refresh as LoadState.Error
            Timber.e(errorState.error)
        }

        LoadingContent(
            empty = false,
            emptyContent = { FullScreenLoading() },
            loading = loadStates.refresh == LoadState.Loading,
            onRefresh = { pagingItems.refresh() }) {
            ChartList(chartData = pagingItems, actionHandler)
        }
    }
}

@Composable
private fun ChartArtistListItem(name: String, listener: Long, index: Int, onClicked: () -> Unit) {
    RankingListItem(
        title = name,
        subtitle = "${listener.abbreviate()} ${stringResource(id = R.string.stats_listeners)}",
        index = index,
        onClicked = onClicked
    )
}

@Composable
private fun ChartTrackListItem(name: String, artist: String, index: Int, onClicked: () -> Unit) {
    RankingListItem(title = name, subtitle = artist, index = index, onClicked = onClicked)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RankingListItem(title: String, subtitle: String, index: Int, onClicked: () -> Unit) {
    ListItem(
        text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { IndexListIconBackground(index = index) },
        modifier = Modifier.clickable(onClick = { onClicked() }),
    )
}