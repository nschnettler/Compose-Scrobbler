package de.schnettler.scrobbler.screens

import android.icu.text.CompactDecimalFormat
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.livedata.observeAsState
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.LiveDataListComponent
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.model.ListItem
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import timber.log.Timber
import java.util.*

val formatter: CompactDecimalFormat = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)

@Composable
fun ChartScreen(model: ChartsViewModel) {
    val artistResponse by model.artistResponse.observeAsState()

    when (artistResponse) {
        is StoreResponse.Loading -> LiveDataLoadingComponent()
        is StoreResponse.Data -> ArtistListItem((artistResponse as StoreResponse.Data<List<Artist>>).value)
        is StoreResponse.Error.Exception -> Timber.d((artistResponse as StoreResponse.Error.Exception<List<Artist>>).errorMessageOrNull())
        is StoreResponse.Error.Message -> Timber.d((artistResponse as StoreResponse.Error.Message<List<Artist>>).message)
    }
}

@Composable
fun ArtistListItem(artistList: List<Artist>) {
    val newList = artistList.map {
        ListItem(
            title = it.name,
            subtitle = "${formatter.format(it.listeners)} Listener ⦁ ${formatter.format(it.playcount)} Plays",
            imageUrl = ""
        )
    }
    LiveDataListComponent(items = newList)
}