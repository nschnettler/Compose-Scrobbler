package de.schnettler.scrobbler

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.ui.core.Alignment.Companion.CenterHorizontally
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentWidth
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist

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
        Card(shape = RoundedCornerShape(4.dp), color = Color.White,
            modifier = Modifier.fillMaxWidth() + Modifier.padding(8.dp)) {
            ListItem(text = {
                Text(
                    text = artist.name,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }, secondaryText = {
                Text(
                    text = "Played ${artist.playcount} by ${artist.listeners} listeners",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif, fontSize = 15.sp,
                        fontWeight = FontWeight.Light, color = Color.DarkGray
                    )
                )
            })
        }
    }
}

@Composable
fun LiveDataLoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
        CircularProgressIndicator(modifier = Modifier.wrapContentWidth(CenterHorizontally))
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}