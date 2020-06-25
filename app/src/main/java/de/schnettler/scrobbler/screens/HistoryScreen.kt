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

   when(recentsResponse) {
      is StoreResponse.Data -> {
         GenericAdapterList(data = (recentsResponse as StoreResponse.Data<List<Track>>).value, onListingSelected = onListingSelected)
      }
      is StoreResponse.Error ->  {
         Timber.d("Error ${(recentsResponse as StoreResponse.Error<List<Track>>).errorMessageOrNull()}")
      }
      is StoreResponse.Loading -> LiveDataLoadingComponent()
   }
}