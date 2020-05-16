package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.padding
import androidx.ui.livedata.observeAsState
import androidx.ui.unit.dp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.ArtistInfo
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.viewmodels.DetailViewModel

@Composable
fun DetailScreen(model: DetailViewModel) {
    val info by model.details.observeAsState()

    when(info) {
        is StoreResponse.Data -> {
            VerticalScroller() {
                ExpandingSummary(text = (info as StoreResponse.Data<ArtistInfo>).value.bio, modifier = Modifier.padding(16.dp))
            }
        }
        is StoreResponse.Loading -> {
            LiveDataLoadingComponent()
        }
        is StoreResponse.Error -> {
            Text(text = (info as StoreResponse.Error).errorMessageOrNull() ?: "")
        }
    }
}