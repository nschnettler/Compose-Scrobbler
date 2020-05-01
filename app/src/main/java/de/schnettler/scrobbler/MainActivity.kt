package de.schnettler.scrobbler

import android.icu.text.CompactDecimalFormat
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.ui.core.Alignment.Companion.CenterHorizontally
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentWidth
import androidx.ui.livedata.observeAsState
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import java.util.*

val formatter: CompactDecimalFormat = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MainViewModel by viewModels()

        setContent {
            MaterialTheme {
                LiveDataComponent(artistResponse = model.topArtists)
            }
        }
    }
}

@Composable
fun LiveDataComponent(artistResponse: LiveData<StoreResponse<List<Artist>>>) {
    val artistList by artistResponse.observeAsState()

    when (artistList) {
        is StoreResponse.Loading -> LiveDataLoadingComponent()
        is StoreResponse.Data -> {
            println((artistList as StoreResponse.Data<List<Artist>>).value)
            LiveDataComponentList(personList = (artistList as StoreResponse.Data<List<Artist>>).value)
        }
        is StoreResponse.Error -> println((artistList as StoreResponse.Error<List<Artist>>).errorMessageOrNull())
    }
}

@Composable
fun LiveDataComponentList(personList: List<Artist>) {
    AdapterList(data = personList) { artist ->
        ListItem(
            text = {
                Text(text = artist.name)
            },
            secondaryText = {
                Text(text = "${formatter.format(artist.listeners)} Listener ⦁ ${formatter.format(artist.playcount)} Plays")
            }
        )
        Divider(color = Color(0x0d000000))
    }
}

@Composable
fun LiveDataLoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
        CircularProgressIndicator(modifier = Modifier.wrapContentWidth(CenterHorizontally))
    }
}