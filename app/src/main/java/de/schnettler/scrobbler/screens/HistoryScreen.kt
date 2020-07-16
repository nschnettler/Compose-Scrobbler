package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.padding
import androidx.ui.unit.dp
import de.schnettler.database.models.CommonEntity
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.SwipeRefreshPrograssIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.util.currentData
import de.schnettler.scrobbler.util.loading
import de.schnettler.scrobbler.util.refreshableUiStateFrom
import de.schnettler.scrobbler.util.refreshing
import de.schnettler.scrobbler.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(model: HistoryViewModel, onListingSelected: (CommonEntity) -> Unit) {
   val (state, refresh) = refreshableUiStateFrom(repositoryCall = model::refreshHistory)

   if (state.loading && !state.refreshing) {
      LiveDataLoadingComponent()
   } else {
      SwipeToRefreshLayout(
         refreshingState = state.refreshing,
         onRefresh = { refresh() },
         refreshIndicator = { SwipeRefreshPrograssIndicator() }
      ) {
         state.currentData?.let {tracks ->
            HistoryTrackList(tracks = tracks,
               onTrackSelected = onListingSelected,
               onNowPlayingSelected = onListingSelected,
               modifier = Modifier.padding(bottom = 56.dp)
            )
         }
      }
   }
}