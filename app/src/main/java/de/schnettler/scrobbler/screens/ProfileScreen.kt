package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.TopListScroller
import de.schnettler.scrobbler.model.LoadingState2
import de.schnettler.scrobbler.util.*
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel, onEntrySelected: (ListingMin) -> Unit) {

   val userState by model.userState.collectAsState()

   val artistState by model.artistState.collectAsState()
   val albumState by model.albumState.collectAsState()
   val trackState by model.trackState.collectAsState()

   when(userState) {
      is LoadingState2.Error -> ContextAmbient.current.toast((userState as LoadingState2.Error<User>).errorMsg)
   }

   VerticalScroller(modifier = Modifier.padding(bottom = 56.dp)) {
      Column(modifier = Modifier.padding(bottom = defaultSpacerSize)) {
         userState.data?.let {
            UserInfoComponent(it)
         }
         TopListScroller(title = "Top-Künstler", content = artistState, onEntrySelected = onEntrySelected)
         TopListScroller(title = "Top-Alben", content = albumState, onEntrySelected = onEntrySelected)
         TopListScroller(title = "Top-Titel", content = trackState, onEntrySelected = onEntrySelected)
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