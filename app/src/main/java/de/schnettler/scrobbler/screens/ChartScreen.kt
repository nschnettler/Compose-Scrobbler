package de.schnettler.scrobbler.screens

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onActive
import androidx.compose.runtime.stateFor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.ErrorSnackbar
import de.schnettler.scrobbler.components.LoadingScreen
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.components.Recyclerview
import de.schnettler.scrobbler.components.SwipeRefreshProgressIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.viewmodels.ChartsViewModel

@Composable
fun ChartScreen(model: ChartsViewModel, onListingSelected: (LastFmEntity) -> Unit) {
    onActive { model.startStream() }
    val chartState by model.state.collectAsState()
    val (showSnackbarError, updateShowSnackbarError) = stateFor(chartState) {
        chartState is RefreshableUiState.Error
    }

    Stack(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
        if (chartState.isLoading) { LoadingScreen() } else {
            SwipeToRefreshLayout(
                refreshingState = chartState.isRefreshing,
                onRefresh = { model.refresh() },
                refreshIndicator = { SwipeRefreshProgressIndicator() }
            ) {
                chartState.currentData?.let { charts ->
                    Recyclerview(items = charts) { (entry, artist) ->
                        ChartListItem(artist.name, entry.count) { onListingSelected(artist) }
                        CustomDivider()
                    }
                }
            }
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = { model.refresh() },
            onDismiss = { updateShowSnackbarError(false) },
            state = chartState,
            fallBackMessage = "Unable to refresh charts",
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ChartListItem(name: String, listener: Long, onClicked: () -> Unit) {
    ListItem(
        text = { Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = {
            Text(
                text = "${listener.abbreviate()} Listener", maxLines = 1, overflow = TextOverflow
                    .Ellipsis
            )
        },
        icon = { NameListIcon(title = name) },
        modifier = Modifier.clickable(onClick = { onClicked() })
    )
}