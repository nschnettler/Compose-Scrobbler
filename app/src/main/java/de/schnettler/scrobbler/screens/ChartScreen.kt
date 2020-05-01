package de.schnettler.scrobbler.screens

import android.icu.text.CompactDecimalFormat
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.ui.livedata.observeAsState
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.LiveDataListComponent
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.model.ListItem
import java.util.*

val formatter: CompactDecimalFormat = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)

@Composable
fun ChartScreen(artistResponse: LiveData<StoreResponse<List<Artist>>>) {
    ArtistsComponent(artistResponse)
}

@Composable
fun ArtistsComponent(artistResponse: LiveData<StoreResponse<List<Artist>>>) {
    val artistList by artistResponse.observeAsState()

    when (artistList) {
        is StoreResponse.Loading -> LiveDataLoadingComponent()
        is StoreResponse.Data -> {
            println((artistList as StoreResponse.Data<List<Artist>>).value)
            val list = (artistList as StoreResponse.Data<List<Artist>>).value
            val newList = list.map {
                ListItem(
                    title = it.name,
                    subtitle = "${formatter.format(it.listeners)} Listener ⦁ ${formatter.format(it.playcount)} Plays",
                    imageUrl = ""
                )
            }
            LiveDataListComponent(items = newList)
        }
        is StoreResponse.Error -> println((artistList as StoreResponse.Error<List<Artist>>).errorMessageOrNull())
    }
}