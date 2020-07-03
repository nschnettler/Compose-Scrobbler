package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.livedata.observeAsState
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.components.GenericAdapterList
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.viewmodels.HistoryViewModel
import timber.log.Timber

@Composable
fun HistoryScreen(model: HistoryViewModel, onListingSelected: (ListingMin) -> Unit) {
   val recentsResponse by model.recentTracks.observeAsState()
   val refreshing by model.isRefreshing.observeAsState()

   when(refreshing) {
      true -> LiveDataLoadingComponent()
      false -> recentsResponse?.let {
         GenericAdapterList(data = it, onListingSelected = onListingSelected)
      }
   }
}