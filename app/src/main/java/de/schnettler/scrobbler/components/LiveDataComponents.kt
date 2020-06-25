package de.schnettler.scrobbler.components

import android.content.Context
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.unit.dp
import com.koduok.compose.navigation.core.BackStack
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.BackStack
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.AppRoute
import de.schnettler.scrobbler.screens.formatter
import de.schnettler.scrobbler.util.firstLetter

@Composable
fun LiveDataLoadingComponent(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator(
            color = colorResource(id = R.color.colorAccent),
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }
}

@Composable
fun GenericAdapterList(data: List<ListingMin>, onListingSelected: (ListingMin) -> Unit) {
    AdapterList(data = data) {item ->
        when(item) {
            is Track -> HistoryItem(
                listing = item,
                subTitle = "${item.artist} ⦁ ${item.album}",
                onListingSelected = onListingSelected
            )
            is Artist -> HistoryItem(listing = item,
                subTitle = "${formatter.format(item.listeners)} Listener ⦁ ${ formatter.format(item.plays)} Plays",
                onListingSelected = onListingSelected
            )
        }
        Divider(color = Color(0x0d000000))
    }
}

@Composable
fun HistoryItem(listing: ListingMin, subTitle: String, onListingSelected: (ListingMin) -> Unit) {
    ListItem(
        text = { Text(text = listing.name) },
        secondaryText = { Text(text = subTitle) },
        icon = {
            Surface(
                color = colorResource(id = R.color.colorBackgroundElevated),
                shape = CircleShape,
                modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)) {
                Box(gravity = ContentGravity.Center) {
                    Text(text = listing.name.firstLetter())
                }
            }
        },
        onClick = { onListingSelected.invoke(listing) }
    )
}