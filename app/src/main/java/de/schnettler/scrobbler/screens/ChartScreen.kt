package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.TopListArtist
import de.schnettler.scrobbler.components.GenericAdapterList
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import timber.log.Timber

@Composable
fun ChartScreen(model: ChartsViewModel, onListingSelected: (LastFmEntity) -> Unit) {
    val artistResponse by model.artistResponse.collectAsState(StoreResponse.Loading(ResponseOrigin.Fetcher))

    when (artistResponse) {
        is StoreResponse.Loading -> LiveDataLoadingComponent()
        is StoreResponse.Data -> GenericAdapterList(
            (artistResponse as StoreResponse.Data<List<TopListArtist>>).value,
            onListingSelected
        )
        is StoreResponse.Error.Exception -> {
            Timber.d((artistResponse as StoreResponse.Error.Exception<List<TopListArtist>>).errorMessageOrNull())
        }
        is StoreResponse.Error.Message -> {
            Timber.d((artistResponse as StoreResponse.Error.Message<List<TopListArtist>>).message)
        }
        is StoreResponse.NoNewData -> { }
    }
}