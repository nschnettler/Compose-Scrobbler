package de.schnettler.scrobbler.screens

import android.content.Intent
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.Color
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.unit.dp
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.StatusTrack
import de.schnettler.scrobble.MediaListenerService
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.*
import de.schnettler.scrobbler.screens.preview.FakeHistoryTrackProvider
import de.schnettler.scrobbler.util.*
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import timber.log.Timber

@Composable
fun LocalScreen(localViewModel: LocalViewModel, onListingSelected: (CommonEntity) -> Unit) {
   val context = ContextAmbient.current
   when(MediaListenerService.isEnabled(context)) {
      true -> Content(localViewModel = localViewModel, onListingSelected = onListingSelected)
      false -> Button(onClick = {
         context.startActivity( Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
      }) {
         Text(text = "Enable")
      }
   }
   Timber.i("Started Service")
}

@Composable
fun Content(localViewModel: LocalViewModel, onListingSelected: (CommonEntity) -> Unit) {
   val recentTracksState by localViewModel.recentTracksState.collectAsState()
   var showDialog by state { false }
   val selectedTrack: MutableState<LocalTrack?> = state { null }
   val (showSnackbarError, updateShowSnackbarError) = stateFor(recentTracksState) {
      recentTracksState is RefreshableUiState.Error
   }

   Stack(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
      if (recentTracksState.loading) {
         LiveDataLoadingComponent()
      } else {
         SwipeToRefreshLayout(
                 refreshingState = recentTracksState.refreshing,
                 onRefresh = { localViewModel.refresh() },
                 refreshIndicator = { SwipeRefreshPrograssIndicator() }
         ) {
            recentTracksState.currentData?.let { list ->
               HistoryTrackList(
                       tracks = list,
                       onTrackSelected = { track, actionType ->
                          when (actionType) {
                             HistoryActionType.EDIT -> {
                                selectedTrack.value = track
                                showDialog = true
                             }
                             HistoryActionType.DELETE -> {
                                // DELETE A TRACK
                             }
                             HistoryActionType.OPEN -> {
                                onListingSelected(track)
                             }
                          }

                       },
                       onNowPlayingSelected = {
                          Timber.d("Update NowPlaying")
                       }
               )
            }
         }
      }
      ErrorSnackbar(
              showError = showSnackbarError,
              onErrorAction = { localViewModel.refresh() },
              onDismiss = { updateShowSnackbarError(false) },
              modifier = Modifier.gravity(Alignment.BottomCenter)
      )
   }

   if (showDialog) {
      TrackEditDialog(onSelect = {track ->
         showDialog = false
         track?.let {updatedTrack ->
            Timber.d("[LocalEdit - Old]${selectedTrack.value}")
            Timber.d("[LocalEdit - New]$updatedTrack")
         }
      }, onDismiss = {
         showDialog = false
      }, track = selectedTrack.value)
   }
}

@Composable
private fun TrackEditDialog(track: LocalTrack?, onSelect: (selected: LocalTrack?) -> Unit, onDismiss: () -> Unit) {
   if (track != null) {
      val trackState = state { TextFieldValue(track.name) }
      val artistState = state { TextFieldValue(track.artist) }
      val albumState = state { TextFieldValue(track.album) }

      AlertDialog(
         onCloseRequest = {
            if (track.copyByState(trackState, artistState, albumState) == track) {
               onDismiss()
            }
         },
         title = { Text(text = "Scrobble bearbeiten") },
         text = {
            Column {
               FilledTextField(value = trackState.value, onValueChange = {trackState.value = it}, label = { Text("Song") })
               Spacer(modifier = Modifier.preferredHeight(16.dp))
               FilledTextField(value = artistState.value, onValueChange = {artistState.value = it}, label = { Text("Künstler") })
               Spacer(modifier = Modifier.preferredHeight(16.dp))
               FilledTextField(value = albumState.value, onValueChange = {albumState.value = it}, label = { Text("Album") })
            }
         },
         confirmButton = {
            val updated = track.copyByState(trackState, artistState, albumState)
            TextButton(
               onClick = { onSelect(if (updated != track) updated else null) },
               contentColor = MaterialTheme.colors.secondary
            ) {
               Text(text = "Select")
            }
         },
         dismissButton = {
            TextButton(onClick = onDismiss, contentColor = EmphasisAmbient.current.medium.applyEmphasis(
               contentColor())) {
               Text(text = "Discard")
            }
         }
      )
   }
}

@Composable
fun <T: StatusTrack> NowPlayingTrack(track: T, onClick: (T) -> Unit) {
   Card(modifier = Modifier.padding(16.dp)) {
      ListItem(
         text = { Text(track.name) },
         secondaryText = { Text(track.artist) },
         icon = {
            PlainListIconBackground(color = R.color.colorAccent) {
               Icon(
                  asset = vectorResource(id = R.drawable.ic_round_music_note_24),
                  tint = Color.White
               )
            }
         },
         onClick = { onClick.invoke(track) }
      )
   }
}

@Composable
fun ScrobbledTrack(track: LocalTrack, onClick: (LocalTrack, HistoryActionType) -> Unit) {
   var expanded by state { false }
   ListItem(
      text = { Text(text = track.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
      secondaryText = {
         Column {
            Text(text = "${track.artist} ⦁ ${track.album}", maxLines = if (expanded) 3 else 1, overflow = TextOverflow.Ellipsis)
            if (expanded) {
               if (track.isLocal()) ComposeAdditionalInformation(track)
               else Spacer(modifier = Modifier.preferredHeight(16.dp))
               Divider()
               ComposeQuickActions(track, onClick)
            }
         }
      },
      icon = { NameListIcon(title = track.name) },
      onClick = { expanded = !expanded},
      trailing = { track.timestampToRelativeTime()?.let {
         Column() {
            Text(text = it)
            Row() {
               if (track.isCached()) {
                  Icon(asset = vectorResource(id = R.drawable.ic_round_cloud_off_24))
                  Spacer(modifier = Modifier.preferredWidth(8.dp))
               }
               if (track.isLocal()) Text(text = "${track.playPercent()} %")
            }
         }
      } }
   )
   Divider(color = colorResource(id = R.color.colorStroke))
}

@Composable
fun ComposeAdditionalInformation(track: LocalTrack) {
   Spacer(modifier = Modifier.preferredHeight(8.dp))
   Text(text = "Source: ${packageNameToAppName(track.playedBy)}")
   Text(text = "Runtime: ${milliSecondsToMinSeconds(track.amountPlayed)}/${milliSecondsToMinSeconds(track.duration)} (${track.playPercent()}%)")
   Text(text = "Timestamp: ${(track.timestamp * 1000).milliSecondsToDate()}")
   Spacer(modifier = Modifier.preferredHeight(8.dp))
}

@Composable
fun ComposeQuickActions(track: LocalTrack, onClick: (LocalTrack, HistoryActionType) -> Unit) {
   val actions = mutableListOf<Pair<@androidx.annotation.DrawableRes Int, () -> Unit>>()
   if (track.isCached()) {
      actions.add(R.drawable.ic_outline_edit_32 to { onClick.invoke(track, HistoryActionType.EDIT) })
      actions.add(R.drawable.ic_round_delete_outline_32 to { onClick.invoke(track, HistoryActionType.DELETE) })
   }
   actions.add(R.drawable.ic_round_open_in_24 to { onClick.invoke(track, HistoryActionType.OPEN) })
   QuickActionsRow(items = actions)
}

@Preview
@Composable
fun HistoryTrack(
   @PreviewParameter(FakeHistoryTrackProvider::class) track: LocalTrack,
   onTrackSelected: (LocalTrack, HistoryActionType) -> Unit = {t1, t2 -> },
   onNowPlayingSelected: (LocalTrack) -> Unit = { }
) {
   if(track.isPlaying()) {
      NowPlayingTrack(track = track, onClick = onNowPlayingSelected)
   } else {
      ScrobbledTrack(track = track, onClick = onTrackSelected)
   }
}


@Composable
fun HistoryTrackList(
   tracks: List<LocalTrack>,
   onTrackSelected: (LocalTrack, HistoryActionType) -> Unit,
   onNowPlayingSelected: (LocalTrack) -> Unit,
   modifier: Modifier = Modifier
) {
   LazyColumnItems(items = tracks, modifier = modifier) {track ->
      HistoryTrack(
         track = track,
         onTrackSelected = onTrackSelected,
         onNowPlayingSelected = onNowPlayingSelected
      )
   }
}

enum class HistoryActionType() {
   EDIT,
   DELETE,
   OPEN
}