package de.schnettler.scrobbler.ui.charts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.common.compose.util.PreviewUtils
import de.schnettler.scrobbler.ui.common.compose.util.ThemedPreview
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider
import de.schnettler.scrobbler.ui.common.compose.widget.FullScreenError
import de.schnettler.scrobbler.ui.common.compose.widget.FullScreenLoading
import de.schnettler.scrobbler.ui.common.compose.widget.IndexListIconBackground
import de.schnettler.scrobbler.ui.common.compose.widget.LoadingContent
import de.schnettler.scrobbler.ui.common.compose.widget.TabbedPager
import de.schnettler.scrobbler.ui.common.util.abbreviate

@Composable
fun ChartScreen(
    viewModel: ChartViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabbedPager(
        pages = ChartTab.values().map { it.text },
        modifier = modifier,
    ) { page ->
        ChartPage(page, viewModel, errorHandler, actionHandler)
    }
}

@Composable
private fun ChartPage(
    pageIndex: Int,
    viewModel: ChartViewModel,
    errorHandler: @Composable (UIError) -> Unit,
    actionHandler: (UIAction) -> Unit
) {
    val currentTab = ChartTab.values()[pageIndex]
    val state by when (currentTab) {
        ChartTab.Artist -> viewModel.artistState.collectAsState()
        ChartTab.Track -> viewModel.trackState.collectAsState()
    }

    if (state.isError) {
        errorHandler(
            UIError.ShowErrorSnackbar(
                state = state,
                fallbackMessage = stringResource(id = R.string.error_charts),
                onAction = { viewModel.refresh(currentTab) }
            ))
    }

    LoadingContent(
        empty = state.isInitialLoading,
        emptyContent = { FullScreenLoading() },
        loading = state.isRefreshLoading,
        onRefresh = { viewModel.refresh(currentTab) }) {
        state.currentData?.let {
            ChartList(it, actionHandler)
        } ?: FullScreenError()
    }
}

@Composable
private fun ChartList(
    chartData: List<Toplist>,
    handler: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) = LazyColumn(modifier) {
    itemsIndexed(items = chartData) { index, entry ->
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

// Preview

@Preview
@Composable
fun ChartListPreview() = ThemedPreview() {
    ChartList(chartData = PreviewUtils.generateFakeArtistCharts(5), { },)
}