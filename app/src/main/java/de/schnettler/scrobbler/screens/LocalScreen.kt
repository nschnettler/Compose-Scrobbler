package de.schnettler.scrobbler.screens

import android.content.Intent
import androidx.compose.*
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
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
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.StatusTrack
import de.schnettler.scrobble.MediaListenerService
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.screens.preview.FakeHistoryTrackProvider
import de.schnettler.scrobbler.util.copyByState
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import timber.log.Timber

@Composable
fun LocalScreen(localViewModel: LocalViewModel) {
   val context = ContextAmbient.current
   when(MediaListenerService.isEnabled(context)) {
      true -> Content(localViewModel = localViewModel)
      false -> Button(onClick = {
         context.startActivity( Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
      }, text = {Text(text = "Enable")})
   }
   Timber.i("Started Service")
}

@Composable
fun Content(localViewModel: LocalViewModel) {
   val data by localViewModel.data.collectAsState()
   var showDialog by state { false }
   val selectedTrack: MutableState<LocalTrack?> = state { null }

   data?.let { list ->
      HistoryTrackList(
         tracks = list,
         onTrackSelected = {
            selectedTrack.value = it
            showDialog = true
         },
         onNowPlayingSelected = {
            Timber.d("Update NowPlaying")
         }
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
            TextButton(onClick = onDismiss, contentColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.5F)) {
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
fun <T: StatusTrack> ScrobbledTrack(track: T, onClick: (T) -> Unit) {
   ListItem(
      text = { Text(text = track.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
      secondaryText = { Text(text = "${track.artist} ⦁ ${track.album}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
      icon = { NameListIcon(title = track.name) },
      onClick = { onClick.invoke(track) },
      trailing = { track.timestampToRelativeTime()?.let {
         Column() {
            Text(text = it)
            Row() {
               if (track.isLocal()) {
                  Icon(asset = vectorResource(id = R.drawable.ic_round_cloud_off_24))
                  Spacer(modifier = Modifier.preferredWidth(8.dp))
               }
               if (track is LocalTrack) Text(text = "${track.playPercent()} %")
            }
         }
      } }
   )
   Divider(color = colorResource(id = R.color.colorStroke))
}

@Preview
@Composable
fun <T: StatusTrack> HistoryTrack(
   @PreviewParameter(FakeHistoryTrackProvider::class) track: T,
   onTrackSelected: (T) -> Unit = { },
   onNowPlayingSelected: (T) -> Unit = { }
) {
   if(track.isPlaying()) {
      NowPlayingTrack(track = track, onClick = onNowPlayingSelected)
   } else {
      ScrobbledTrack(track = track, onClick = onTrackSelected)
   }
}


@Composable
fun <T: StatusTrack> HistoryTrackList(
   tracks: List<T>,
   onTrackSelected: (T) -> Unit,
   onNowPlayingSelected: (T) -> Unit
) {
   LazyColumnItems(items = tracks) {track ->
      HistoryTrack(
         track = track,
         onTrackSelected = onTrackSelected,
         onNowPlayingSelected = onNowPlayingSelected
      )
   }
}