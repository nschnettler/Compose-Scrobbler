package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.TitleWithLoadingIndicator
import de.schnettler.scrobbler.model.LoadingState
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
fun ProfileScreen(model: UserViewModel, onEntrySelected: (ListingMin) -> Unit) {

   val userState by model.userState.collectAsState(null)
   val artistState by model.artistState.collectAsState(null)
   val albumState by model.albumState.collectAsState(null)
   val trackState by model.trackState.collectAsState(null)

   VerticalScroller(modifier = Modifier.padding(bottom = 56.dp)) {
      Column(modifier = Modifier.padding(bottom = defaultSpacerSize)) {
         userState?.data?.let {
            UserInfoComponent(it)
         }
         TopEntry2(title = "Top-Künstler", content = artistState, onEntrySelected = onEntrySelected)
         TopEntry2(title = "Top-Alben", content = albumState, onEntrySelected = onEntrySelected)
         TopEntry2(title = "Top-Titel", content = trackState, onEntrySelected = onEntrySelected)
      }
   }
}

@Composable
fun TopEntry2(
   title: String,
   content: LoadingState<List<TopListEntryWithData>>?,
   onEntrySelected: (ListingMin) -> Unit
) {
   TitleWithLoadingIndicator(title = title, loading = content?.loading ?: true)

   content?.data?.let {data ->
      HorizontalScrollableComponent2(
         content = content.data.map { Pair(it.data, it.topListEntry.count) },
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
      Column() {
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

         Row(modifier = Modifier.fillMaxWidth() + Modifier.padding(bottom = 16.dp, top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
               Icon(asset = vectorResource(id = R.drawable.ic_round_play_circle_outline_24))
               Text(text = formatter.format(user.playcount))
            }
            Column(horizontalGravity = Alignment.CenterHorizontally) {
               Icon(asset = vectorResource(id = R.drawable.account_music_outline))
               Text(text = formatter.format(user.artistCount))
            }
            Column(horizontalGravity = Alignment.CenterHorizontally) {
               Icon(asset = vectorResource(id = R.drawable.ic_round_favorite_border_32))
               Text(text = formatter.format(user.lovedTracksCount))
            }
         }
      }
   }
}

@Composable
fun HorizontalScrollableComponent(
   content: List<ListingMin>,
   onEntrySelected: (ListingMin) -> Unit,
   width: Dp,
   height: Dp,
   showPlays: Boolean,
   hintTextSize: TextUnit = 62.sp
) {
   HorizontalScroller(modifier = Modifier.fillMaxWidth()) {
      Row {
         for(entry in content) {
            AlbumItem(onEntrySelected = onEntrySelected, entry = entry, width = width, height = height, showPlays = showPlays, hintTextSize = hintTextSize)
         }
      }
   }
}


@Composable
fun AlbumItem(onEntrySelected: (ListingMin) -> Unit, entry: ListingMin, width: Dp, height: Dp, showPlays: Boolean, hintTextSize: TextUnit = 62.sp) {
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
            if (showPlays) {
               Text("${formatter.format(entry.plays)} Wiedergaben",
                  style = TextStyle(
                     fontSize = 12.sp
                  )
               )
            }
         }
      }
   }
}

@Composable
fun HorizontalScrollableComponent2(
   content: List<Pair<ListingMin, Long>>,
   onEntrySelected: (ListingMin) -> Unit,
   width: Dp,
   height: Dp,
   subtitleSuffix: String = "",
   hintTextSize: TextUnit = 62.sp
) {
   HorizontalScroller(modifier = Modifier.fillMaxWidth()) {
      Row {
         for(entry in content) {
            val data = entry.first
            val count = entry.second
            Clickable(onClick = {
               onEntrySelected.invoke(data)
            }, modifier = Modifier.ripple()) {
               Column(modifier = Modifier.preferredWidth(width) + Modifier.padding(horizontal = 8.dp)) {
                  Card(shape = RoundedCornerShape(cardCornerRadius),
                     modifier = Modifier.preferredWidth(width) + Modifier.preferredHeight(height - 8.dp)
                  ) {
                     when (val imageUrl = data.imageUrl) {
                        null -> {
                           Box(gravity = ContentGravity.Center) {
                              Text(text = data.name.firstLetter(), style = TextStyle(fontSize = hintTextSize))
                           }
                        }
                        else -> {
                           CoilImageWithCrossfade(data = imageUrl, contentScale = ContentScale.Crop)
                        }
                     }
                  }
                  Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)) {
                     Text(data.name,
                        style = TextStyle(
                           fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                     )
                     Text("${formatter.format(count)} $subtitleSuffix",
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