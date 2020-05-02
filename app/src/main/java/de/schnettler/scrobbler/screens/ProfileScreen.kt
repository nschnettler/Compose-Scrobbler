package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.User
import de.schnettler.scrobbler.viewmodels.UserViewModel
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel) {

   val userResponse by model.userInfo.observeAsState()

   when(userResponse) {
      is StoreResponse.Data -> {
         Column {
            UserInfoComponent((userResponse as StoreResponse.Data<User>).value)
         }
      }
      is StoreResponse.Loading ->  {
         LiveDataLoadingComponent()
      }
      is StoreResponse.Error ->  {
         Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
            Text(text = "Error: ${(userResponse as StoreResponse.Error<User>).errorMessageOrNull()}")
         }
      }
   }
}

@Composable
fun UserInfoComponent(user: User) {
   Card(modifier = Modifier.preferredHeight(120.dp) + Modifier.fillMaxWidth() + Modifier.padding(8.dp),
      shape = RoundedCornerShape(8.dp)
   ) {
      ConstraintLayout(ConstraintSet {
         val title = tag("title")
         val subtitle = tag("subtitle")
         val image = tag("image")

         image.apply {
            centerVertically()
            left constrainTo parent.left
            left.margin = 16.dp
         }

         title.apply {
            left constrainTo image.right
            left.margin = 16.dp
            top constrainTo image.top
         }

         subtitle.apply {
            bottom constrainTo image.bottom
            left constrainTo image.right
            left.margin = 16.dp
         }

      }) {
         Text(user.name, style = TextStyle(fontFamily = FontFamily.Serif, fontWeight =
         FontWeight.W900, fontSize = 14.sp), modifier = Modifier.tag("title"))
         val date: LocalDateTime = Instant.ofEpochSecond(user.registerDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
         Text("scrobbling since ${DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date)}", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight =
         FontWeight.W900, fontSize = 14.sp), modifier = Modifier.tag("subtitle"))
         Box(modifier = Modifier.tag("image") + Modifier.preferredHeight(72.dp) +
                 Modifier.preferredWidth(72.dp)) {
            //Image(imageFromResource(resources, R.drawable.lenna))
         }
      }
   }
}