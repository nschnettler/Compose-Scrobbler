package de.schnettler.scrobbler.screens

import android.icu.text.CompactDecimalFormat
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.ui.livedata.observeAsState
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.LiveDataListComponent
import de.schnettler.scrobbler.model.ListItem
import timber.log.Timber
import java.util.*

val formatter: CompactDecimalFormat = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)

@Composable
fun ChartScreen(artistResponse: LiveData<List<Artist>>) {
    val response by artistResponse.observeAsState(listOf())

    Timber.d("Loaded: $response")
    ArtistsComponent(artistList = response)
//    when (response) {
//        is StoreResponse.Loading -> LiveDataLoadingComponent()
//        is StoreResponse.Data -> ArtistsComponent((response as StoreResponse.Data<List<Artist>>).value)
//        is StoreResponse.Error.Exception -> Timber.d((response as StoreResponse.Error.Exception<List<Artist>>).errorMessageOrNull())
//        is StoreResponse.Error.Message -> Timber.d((response as StoreResponse.Error.Message<List<Artist>>).message)
//    }


}

@Composable
fun ArtistsComponent(artistList: List<Artist>) {
    val newList = artistList.map {
        ListItem(
            title = it.name,
            subtitle = "${formatter.format(it.listeners)} Listener ⦁ ${formatter.format(it.playcount)} Plays",
            imageUrl = ""
        )
    }
    LiveDataListComponent(items = newList)
}