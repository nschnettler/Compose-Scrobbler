package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.viewmodels.ChartsViewModel

@Composable
fun ChartScreen(model: ChartsViewModel, onListingSelected: (LastFmEntity) -> Unit) {
//    val artistResponse by model.artistResponse.observeAsState()
//
//    when (artistResponse) {
//        is StoreResponse.Loading -> LiveDataLoadingComponent()
//        is StoreResponse.Data -> GenericAdapterList(
//            (artistResponse as StoreResponse.Data<List<Artist>>).value,
//            onListingSelected
//        )
//        is StoreResponse.Error.Exception -> {
//            Timber.d((artistResponse as StoreResponse.Error.Exception<List<Artist>>).errorMessageOrNull())
//        }
//        is StoreResponse.Error.Message -> {
//            Timber.d((artistResponse as StoreResponse.Error.Message<List<Artist>>).message)
//        }
//    }
}