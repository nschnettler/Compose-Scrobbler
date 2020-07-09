package de.schnettler.scrobbler.screens

import androidx.compose.*
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.res.colorResource
import androidx.ui.unit.dp
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.User
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.StatsRow
import de.schnettler.scrobbler.components.TopListScroller
import de.schnettler.scrobbler.util.*
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel, onListingSelected: (ListingMin) -> Unit) {

   val userState by model.userState.collectAsState()

   val artistState by model.artistState.collectAsState()
   val albumState by model.albumState.collectAsState()
   val trackState by model.trackState.collectAsState()
   val timePeriod by model.timePeriod.collectAsState()
   val showDialog by model.showFilterDialog.collectAsState()

   if (showDialog) {
      PeriodSelectDialog(onSelect = { newTimePeriod ->
         model.updatePeriod(newTimePeriod)
         model.showDialog(false)
      }, onDismiss = {
         model.showDialog(false)
      }, model = model)
   }

   userState.handleIfError(ContextAmbient.current)

   VerticalScroller(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
         userState.data?.let {
            UserInfoComponent(it)
         }
         TopListScroller(title = "Top-Künstler (${timePeriod.niceName})", content = artistState, onEntrySelected =
         onListingSelected)
         TopListScroller(title = "Top-Alben", content = albumState, onEntrySelected = onListingSelected)
         TopListScroller(title = "Top-Titel", content = trackState, onEntrySelected = onListingSelected)
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
      Column {
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
                        CoilImage(data = user.imageUrl, modifier = Modifier.fillMaxSize())
                     }
                  }
               }
            }
         )

         StatsRow(items = listOf(
            R.drawable.ic_round_play_circle_outline_24 to user.playcount,
            R.drawable.account_music_outline to user.artistCount,
            R.drawable.ic_round_favorite_border_32 to user.lovedTracksCount
         ))
      }
   }
}

@Composable
private fun PeriodSelectDialog(onSelect: (selected: TimePeriod) -> Unit, onDismiss: () -> Unit, model: UserViewModel) {
   var selected by state { model.timePeriod.value }
   val radioGroupOptions = TimePeriod.values().asList()
   AlertDialog(
           onCloseRequest = { onDismiss() },
           title = { Text(text = "Zeitrahmen") },
           text = {
              RadioGroup {
                 radioGroupOptions.forEach {
                    RadioGroupTextItem(selected = selected == it, onSelect = {
                       selected = it
                    }, text = it.niceName)
                 }
              }
           },
           confirmButton = {
              TextButton(onClick = { onSelect(selected) }, contentColor = MaterialTheme.colors.secondary) {
                 Text(text = "Select")
              }
           }
   )
}