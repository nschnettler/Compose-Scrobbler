package de.schnettler.scrobbler.screens

import androidx.compose.foundation.Text
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.TopListArtist
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.LoadingScreen
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.components.Recyclerview
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import timber.log.Timber

@Composable
fun ChartScreen(model: ChartsViewModel, onListingSelected: (LastFmEntity) -> Unit) {
    val artistResponse by model.artistResponse.collectAsState(StoreResponse.Loading(ResponseOrigin.Fetcher))

    when (artistResponse) {
        is StoreResponse.Loading -> LoadingScreen()
        is StoreResponse.Data -> {
            Recyclerview(items = (artistResponse as StoreResponse.Data<List<TopListArtist>>).value) { (entry, artist) ->
                ChartListItem(artist.name, entry.count) { onListingSelected(artist) }
                CustomDivider()
            }
        }
        is StoreResponse.Error.Exception -> {
            Timber.d((artistResponse as StoreResponse.Error.Exception<List<TopListArtist>>).errorMessageOrNull())
        }
        is StoreResponse.Error.Message -> {
            Timber.d((artistResponse as StoreResponse.Error.Message<List<TopListArtist>>).message)
        }
        is StoreResponse.NoNewData -> { }
    }
}

@Composable
private fun ChartListItem(name: String, listener: Long, onClicked: () -> Unit) {
    ListItem(
        text = { Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = "${listener.abbreviate()} Listener", maxLines = 1, overflow = TextOverflow
            .Ellipsis) },
        icon = { NameListIcon(title = name) },
        onClick = { onClicked() }
    )
}