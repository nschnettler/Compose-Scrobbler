package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
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
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.TitleWithLoadingIndicator
import de.schnettler.scrobbler.util.*
import de.schnettler.scrobbler.viewmodels.LoadingState
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel, onEntrySelected: (ListingMin) -> Unit) {

   val userResponse by model.userInfo.observeAsState()
   val artistState by model.artistState.collectAsState()
   val albumState by model.albumState.collectAsState()
   val trackState by model.trackState.collectAsState()
   println("AlbumState $albumState")

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
         TopEntry(title = "Top-Künstler", content = artistState, onEntrySelected = onEntrySelected)
         TopEntry(title = "Top-Alben", content = albumState, onEntrySelected = onEntrySelected)
         TopEntry(title = "Top-Titel", content = trackState, onEntrySelected = onEntrySelected)
      }
   }
}

@Composable
fun TopEntry(title: String, content: LoadingState<List<ListingMin>>?, onEntrySelected: (ListingMin) -> Unit) {
   TitleWithLoadingIndicator(title = title, loading = content?.loading ?: true)

   content?.let {
      HorizontalScrollableComponent(
         content = it.data,
         onEntrySelected = onEntrySelected,
         width = 172.dp,
         height = 172.dp,
         subtitleSuffix = "Wiedergaben"
      )
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
   content: List<ListingMin>,
   onEntrySelected: (ListingMin) -> Unit,
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
                              Text(text = entry.name.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                           }
                        }
                        else -> {
                           CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop)
                        }
                     }
                  }
                  Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)) {
                     Text(entry.name,
                        style = TextStyle(
                           fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                     )
                     Text("${formatter.format(entry.plays)} $subtitleSuffix",
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