package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.layout.padding
import androidx.ui.livedata.observeAsState
import androidx.ui.unit.dp
import de.schnettler.database.models.CommonEntity
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(model: HistoryViewModel, onListingSelected: (CommonEntity) -> Unit) {
   val recentsResponse by model.recentTracks.observeAsState()
   val refreshing by model.isRefreshing.observeAsState()

   when(refreshing) {
      true -> LiveDataLoadingComponent()
      false -> recentsResponse?.let {
         HistoryTrackList(tracks = it,
            onTrackSelected = onListingSelected,
            onNowPlayingSelected = onListingSelected,
            modifier = Modifier.padding(bottom = 56.dp)
         )
      }
   }
}