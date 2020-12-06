package de.schnettler.scrobbler.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.IndexListIconBackground
import de.schnettler.scrobbler.components.LoadingScreen
import de.schnettler.scrobbler.components.SwipeRefreshProgressIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.screens.charts.ChartTab
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import dev.chrisbanes.accompanist.insets.statusBarsHeight

@Composable
fun ChartScreen(
    model: ChartsViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember {
        mutableStateOf(ChartTab.Artist)
    }

    val chartState by when (selectedTab) {
        ChartTab.Artist -> model.artistState.collectAsState()
        ChartTab.Track -> model.trackState.collectAsState()
    }

    if (chartState.isError) {
        errorHandler(UIError.ShowErrorSnackbar(
            state = chartState,
            fallbackMessage = stringResource(id = R.string.error_charts),
            onAction = { model.refresh(selectedTab) }
        ))
    }

    if (chartState.isLoading) {
        LoadingScreen()
    } else {
        SwipeToRefreshLayout(
            refreshingState = chartState.isRefreshing,
            onRefresh = { model.refresh(selectedTab) },
            refreshIndicator = { SwipeRefreshProgressIndicator() }
        ) {
            Column {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.statusBarsHeight())
                TabRow(
                    selectedTabIndex = selectedTab.index,
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    val onSelect: (ChartTab) -> Unit = { selectedTab = it }
                    ChartsTab(
                        tab = ChartTab.Artist,
                        current = selectedTab,
                        onSelect = onSelect
                    )
                    ChartsTab(
                        tab = ChartTab.Track,
                        current = selectedTab,
                        onSelect = onSelect
                    )
                }
                ChartList(chartState.currentData, actionHandler, modifier)
            }
        }
    }
}

@Composable
private fun ChartList(chartData: List<Toplist>?, handler: (UIAction) -> Unit, modifier: Modifier = Modifier) {
    chartData?.let { charts ->
        LazyColumnForIndexed(items = charts, modifier) { index, entry ->
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
}

@Composable
private fun ChartsTab(tab: ChartTab, current: ChartTab, onSelect: (ChartTab) -> Unit) {
    Tab(selected = tab == current, onClick = { onSelect(tab) }, text = { Text(text = stringResource(id = tab.text)) })
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

@Composable
private fun RankingListItem(title: String, subtitle: String, index: Int, onClicked: () -> Unit) {
    ListItem(
        text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { IndexListIconBackground(index = index) },
        modifier = Modifier.clickable(onClick = { onClicked() }),
    )
}