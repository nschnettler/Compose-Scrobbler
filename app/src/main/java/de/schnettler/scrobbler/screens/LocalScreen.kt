package de.schnettler.scrobbler.screens

import android.content.Intent
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.Color
import androidx.ui.layout.padding
import androidx.ui.material.Button
import androidx.ui.material.Card
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.unit.dp
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.database.models.Track
import de.schnettler.scrobble.MediaListenerService
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.screens.preview.FakeHistoryTrackProvider
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

   data?.let { list ->
      val trackList = list.map { it.mapToLastFmTrack() }.filterIndexed { index, track ->
         if (index == 0) {
            return@filterIndexed track.scrobbleStatus == ScrobbleStatus.PLAYING || track.scrobbleStatus ==
                    ScrobbleStatus.LOCAL
         } else {
            return@filterIndexed track.scrobbleStatus == ScrobbleStatus.LOCAL
         }
      }
      HistoryTrackList(
         tracks = trackList,
         onTrackSelected = {Timber.d("Selected $it")},
         onNowPlayingSelected = { Timber.d("Selected Nowplaying $it")}
      )
   }
}

fun LocalTrack.mapToLastFmTrack() = Track(
        name = title,
        url = "",
        duration = duration,
        artist = artist,
        album = album
).apply {
   timestamp = startTime
   scrobbleStatus = status
}

@Composable
fun NowPlayingTrack(track: Track, onClick: (Track) -> Unit) {
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
fun ScrobbledTrack(track: Track, onClick: (Track) -> Unit) {
   ListItem(
      text = { Text(text = track.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
      secondaryText = { Text(text = "${track.artist} ⦁ ${track.album}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
      icon = { NameListIcon(item = track) },
      onClick = { onClick.invoke(track) },
      trailing = { track.timestampToRelativeTime()?.let { Text(text = it) } }
   )
   Divider(color = colorResource(id = R.color.colorStroke))
}

@Preview
@Composable
fun HistoryTrack(
   @PreviewParameter(FakeHistoryTrackProvider::class) track: Track,
   onTrackSelected: (Track) -> Unit = { },
   onNowPlayingSelected: (Track) -> Unit = { }
) {
   if(track.isPlaying()) {
      NowPlayingTrack(track = track, onClick = onNowPlayingSelected)
   } else {
      ScrobbledTrack(track = track, onClick = onTrackSelected)
   }
}


@Composable
fun HistoryTrackList(
   tracks: List<Track>,
   onTrackSelected: (Track) -> Unit,
   onNowPlayingSelected: (Track) -> Unit
) {
   LazyColumnItems(items = tracks) {track ->
      HistoryTrack(
         track = track,
         onTrackSelected = onTrackSelected,
         onNowPlayingSelected = onNowPlayingSelected
      )
   }
}