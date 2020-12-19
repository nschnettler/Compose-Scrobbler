package de.schnettler.scrobbler.ui.history

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobbler.ui.common.compose.LoadingScreen
import de.schnettler.scrobbler.ui.common.compose.SwipeRefreshProgressIndicator
import de.schnettler.scrobbler.ui.common.compose.SwipeToRefreshLayout
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider
import de.schnettler.scrobbler.ui.history.dialog.ConfirmDialog
import de.schnettler.scrobbler.ui.history.dialog.TrackEditDialog
import de.schnettler.scrobbler.ui.history.model.HistoryError
import de.schnettler.scrobbler.ui.history.model.ScrobbleAction
import de.schnettler.scrobbler.ui.history.model.ScrobbleAction.DELETE
import de.schnettler.scrobbler.ui.history.model.ScrobbleAction.EDIT
import de.schnettler.scrobbler.ui.history.model.ScrobbleAction.OPEN
import de.schnettler.scrobbler.ui.history.model.ScrobbleAction.SUBMIT
import de.schnettler.scrobbler.ui.history.widet.ScrobbleItem
import dev.chrisbanes.accompanist.insets.statusBarsHeight

@Composable
fun HistoryScreen(
    model: LocalViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
    loggedIn: Boolean
) {
    Content(
        localViewModel = model,
        actionHandler = actionHandler,
        errorHandler = errorHandler,
        modifier = modifier,
        loggedIn = loggedIn
    )
}

@Suppress("LongMethod")
@Composable
fun Content(
    localViewModel: LocalViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    loggedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    onActive {
        if (loggedIn) {
            localViewModel.startStream()
        }
    }
    val recentTracksState by localViewModel.state.collectAsState()
    val cachedNumber by localViewModel.cachedScrobblesCOunt.collectAsState(initial = 0)
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val selectedTrack: MutableState<Scrobble?> = remember { mutableStateOf(null) }
    if (recentTracksState.isError && loggedIn) {
        errorHandler(
            UIError.ShowErrorSnackbar(
                state = recentTracksState,
                fallbackMessage = stringResource(id = R.string.error_history),
                onAction = localViewModel::refresh
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (recentTracksState.isLoading) { LoadingScreen() } else {
            SwipeToRefreshLayout(
                refreshingState = recentTracksState.isRefreshing,
                onRefresh = { localViewModel.refresh() },
                refreshIndicator = { SwipeRefreshProgressIndicator() }
            ) {
                recentTracksState.currentData?.let { list ->
                    HistoryTrackList(
                        tracks = list,
                        onActionClicked = { track, actionType ->
                            selectedTrack.value = track
                            when (actionType) {
                                EDIT -> showEditDialog = true
                                DELETE -> showConfirmDialog = true
                                OPEN -> actionHandler(ListingSelected(track.asLastFmTrack()))
                                SUBMIT -> localViewModel.submitScrobble(track)
                            }
                        },
                        onNowPlayingSelected = { },
                        errors = getErrors(AmbientContext.current, loggedIn),
                        onErrorClicked = { error -> actionHandler(error.action) }
                    )
                }
            }
        }
        if (cachedNumber > 0) {
            ExtendedFloatingActionButton(
                text = { Text(text = "$cachedNumber ${stringResource(id = R.string.scrobbles)}") },
                onClick = { localViewModel.scheduleScrobbleSubmission() },
                icon = { Icon(Icons.Outlined.CloudUpload) },
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
            )
        }
    }

    if (showEditDialog) {
        TrackEditDialog(onSelect = { track ->
            showEditDialog = false
            track?.let { updatedTrack -> localViewModel.editCachedScrobble(updatedTrack) }
        }, onDismiss = {
            showEditDialog = false
        }, track = selectedTrack.value)
    }
    if (showConfirmDialog) {
        ConfirmDialog(
            title = stringResource(id = R.string.deletedialog_title),
            description = stringResource(id = R.string.deletedialog_content),
        ) { confirmed ->
            if (confirmed && selectedTrack.value != null) {
                selectedTrack.value?.let { localViewModel.deleteScrobble(it) }
            }
            showConfirmDialog = false
        }
    }
}

private fun getErrors(context: Context, loggedIn: Boolean) = listOfNotNull(
    if (!context.notificationListenerEnabled()) HistoryError.NotificationAccessDisabled else null,
    if (!loggedIn) HistoryError.LoggedOut else null
)

@Composable
fun HistoryTrackList(
    tracks: List<Scrobble>,
    onActionClicked: (Scrobble, ScrobbleAction) -> Unit,
    onNowPlayingSelected: (Scrobble) -> Unit,
    onErrorClicked: (HistoryError) -> Unit,
    errors: List<HistoryError>
) {
    LazyColumn {
        item {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.statusBarsHeight())
        }

        item {
            if (errors.isNotEmpty()) {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column {
                        errors.forEachIndexed { index, error ->
                            ErrorItem(item = error) { onErrorClicked(error) }

                            if (index != errors.size - 1) {
                                CustomDivider(startIndent = 72.dp)
                            }
                        }
                    }
                }
            }
        }

        items(tracks) { track ->
            if (track.isPlaying()) {
                NowPlayingItem(name = track.name, artist = track.artist, onClick = { onNowPlayingSelected(track) })
            } else {
                ScrobbleItem(track = track, onActionClicked = { onActionClicked(track, it) })
            }
        }
    }
}