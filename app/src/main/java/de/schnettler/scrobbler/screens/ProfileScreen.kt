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
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Listing
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.util.*
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel, onEntrySelected: (Listing) -> Unit) {

   val userResponse by model.userInfo.observeAsState()
   val artistResponse by model.userTopArtists.observeAsState()
   val albumResponse  by model.userTopAlbums.observeAsState()
   val tracksResponse by model.topTracks.observeAsState()

   VerticalScroller(modifier = Modifier.padding(bottom = 56.dp)) {
      Column(modifier = Modifier.padding(bottom = defaultSpacerSize)) {
         when(userResponse) {
            is StoreResponse.Data -> {
               UserInfoComponent((userResponse as StoreResponse.Data<User>).value)
            }
            is StoreResponse.Loading ->  { LiveDataLoadingComponent() }
            is StoreResponse.Error ->  {
               Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                  Text(text = "Error: ${(userResponse as StoreResponse.Error<User>).errorMessageOrNull()}")
               }
            }
         }

         TopEntry(title = "Top-Künstler", content = artistResponse, onEntrySelected = onEntrySelected)
         TopEntry(title = "Top-Alben", content = albumResponse, onEntrySelected = onEntrySelected)
         TopEntry(title = "Top-Titel", content = tracksResponse, onEntrySelected = onEntrySelected)
      }
   }
}

@Composable
fun TopEntry(title: String, content: StoreResponse<List<Listing>>?, onEntrySelected: (Listing) -> Unit) {
   TitleComponent(title = title)
   
   when(content) {
      is StoreResponse.Data -> {
         HorizontalScrollableComponent(
            content = content.value,
            onEntrySelected = onEntrySelected,
            width = 172.dp,
            height = 172.dp,
            subtitleSuffix = "Wiedergaben"
         )
      }
      is StoreResponse.Error -> {
         Text(text = content.errorMessageOrNull() ?: "")
      }
      is StoreResponse.Loading -> {
         LiveDataLoadingComponent(modifier = Modifier.height(32.dp) + Modifier.width(32.dp))
      }
   }
}

@Composable
fun UserInfoComponent(user: User) {
   Card(modifier = Modifier.fillMaxWidth() + Modifier.padding(
      start = defaultSpacerSize,
      end = defaultSpacerSize,
      top = defaultSpacerSize
   ), shape = RoundedCornerShape(cardCornerRadius)
   ) {
      val date: LocalDateTime = Instant.ofEpochSecond(user.registerDate)
         .atZone(ZoneId.systemDefault())
         .toLocalDateTime()
      ListItem(
         text = {
            Text(text = "${user.name} ${user.countryCode.toCountryCode()?.toFlagEmoji()}")
         },
         secondaryText = {
            Text(text = "${user.realname}\nscrobbelt seit ${DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date)}")
         },
         icon = {
            Surface(color = colorResource(id = R.color.colorStroke), shape = CircleShape) {
               Box(modifier = Modifier.tag("image") + Modifier.preferredHeight(56.dp) + Modifier.preferredWidth(56.dp)) {
                  if (user.imageUrl.isNotEmpty()) {
                     CoilImage(data = user.imageUrl)
                  }
               }
            }
         }
      )
   }
}

@Composable
fun HorizontalScrollableComponent(
   content: List<Listing>,
   onEntrySelected: (Listing) -> Unit,
   width: Dp,
   height: Dp,
   subtitleSuffix: String = "",
   hintTextSize: TextUnit = 62.sp
) {
   HorizontalScroller(modifier = Modifier.fillMaxWidth()) {
      Row {
         for(entry in content) {
            Clickable(onClick = {
               onEntrySelected.invoke(entry)
            }, modifier = Modifier.ripple()) {
               Column(modifier = Modifier.preferredWidth(width) + Modifier.padding(horizontal = 8.dp)) {
                  Card(shape = RoundedCornerShape(cardCornerRadius),
                     modifier = Modifier.preferredWidth(width) + Modifier.preferredHeight(height - 8.dp)
                  ) {
                     when (val imageUrl = entry.imageUrl) {
                        null -> {
                           Box(gravity = ContentGravity.Center) {
                              Text(text = entry.title.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                           }
                        }
                        else -> {
                           CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop)
                        }
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
                     entry.subtitle?.let {subTitle ->
                        Text("$subTitle $subtitleSuffix",
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
   }
}