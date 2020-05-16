package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.LiveDataListComponent
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import timber.log.Timber

@Composable
fun ProfileScreen(model: UserViewModel) {

   val userResponse by model.userInfo.observeAsState()
   val artistResponse by model.userTopArtists.observeAsState()
   val albumResponse  by model.userTopAlbums.observeAsState()
   val tracksResponse by model.topTracks.observeAsState()

   VerticalScroller(modifier = Modifier.padding(bottom = 56.dp)) {
      Column(modifier = Modifier.padding(bottom = 16.dp)) {
         when(userResponse) {
            is StoreResponse.Data -> {
               Column { UserInfoComponent((userResponse as StoreResponse.Data<User>).value) }
            }
            is StoreResponse.Loading ->  { LiveDataLoadingComponent() }
            is StoreResponse.Error ->  {
               Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                  Text(text = "Error: ${(userResponse as StoreResponse.Error<User>).errorMessageOrNull()}")
               }
            }
         }

         TopEntry(title = "Top-Künstler", content = artistResponse)
         TopEntry(title = "Top-Alben", content = albumResponse)
         TopEntry(title = "Top-Titel", content = tracksResponse)
      }
   }
}

@Composable
fun TopEntry(title: String, content: StoreResponse<List<TopListEntry>>?) {
   TitleComponent(title = title)
   
   when(content) {
      is StoreResponse.Data -> {
         HorizontalScrollableComponent(content = content.value)
      }
      is StoreResponse.Error -> {
         Timber.d("Error ${(content as StoreResponse.Error<*>).errorMessageOrNull()}")
      }
   }
}

@Composable
fun UserInfoComponent(user: User) {
   Card(modifier = Modifier.fillMaxWidth() + Modifier.padding(8.dp),
      shape = RoundedCornerShape(8.dp)
   ) {
      val date: LocalDateTime = Instant.ofEpochSecond(user.registerDate)
         .atZone(ZoneId.systemDefault())
         .toLocalDateTime()
      ListItem(
         text = {
            Text(text = user.name)
         },
         secondaryText = {
            Text(text = "scrobbling since ${DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date)}")
         },
         icon = {
            Surface(color = colorResource(id = R.color.colorStroke), shape = CircleShape) {
               Box(modifier = Modifier.tag("image") + Modifier.preferredHeight(72.dp) + Modifier.preferredWidth(72.dp)) {
//            //Image(imageFromResource(resources, R.drawable.lenna))
               }
            }

         }
      )
   }
}

@Composable
fun UserArtistsComponent(artistList: List<Artist>) {
   val newList = artistList.map {
      de.schnettler.scrobbler.model.ListItem(
         title = it.name,
         subtitle = "${formatter.format(it.playcount)} Plays",
         imageUrl = ""
      )
   }
   LiveDataListComponent(items = newList)
}

@Composable
fun HorizontalScrollableComponent(content: List<TopListEntry>) {
   HorizontalScroller(modifier = Modifier.fillMaxWidth()) {
      Row {
         for(entry in content) {
            Column(modifier = Modifier.preferredWidth(172.dp) + Modifier.padding(end = 8.dp, start = 8.dp)) {
               Card(shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.preferredWidth(172.dp) + Modifier.preferredHeight(172.dp)
               ) {
                  entry.imageUrl?.let {
                     CoilImageWithCrossfade(data = it, contentScale = ContentScale.Crop)
                  }
               }
               Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)) {
                  Text(entry.title,
                     style = TextStyle(
                        fontSize = 14.sp
                     ),
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                  )
                  Text("${entry.plays} Wiedergaben",
                     style = TextStyle(
                        fontSize = 12.sp
                     )
                  )
               }
            }
         }
      }
   }
}