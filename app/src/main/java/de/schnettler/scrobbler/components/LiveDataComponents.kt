package de.schnettler.scrobbler.components

import android.text.format.DateUtils
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.R
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
                trailingText = secondsToRelativeTime(item.timestamp),
                onListingSelected = onListingSelected
            )
            is Artist -> HistoryItem(listing = item,
                subTitle = "${formatter.format(item.listeners)} Listener ⦁ ${ formatter.format(item.plays)} Plays",
                onListingSelected = onListingSelected
            )
        }
        Divider(color = colorResource(id = R.color.colorStroke))
    }
}

fun secondsToRelativeTime(time: Long) =
        if (time > 0) {
            DateUtils.getRelativeTimeSpanString(time * 1000, System.currentTimeMillis(), DateUtils
                    .MINUTE_IN_MILLIS).toString()
        } else null

@Composable
fun HistoryItem(
        listing: ListingMin,
        subTitle: String,
        onListingSelected: (ListingMin) -> Unit,
        trailingText: String? = null) {
    ListItem(
        text = { Text(text = listing.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = subTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
        onClick = { onListingSelected.invoke(listing) },
        trailing = { trailingText?.let { Text(text = it)} }
    )
}


@Preview
@Composable
fun testPreview() {
    //ThemedPreview() {
    HistoryItem(listing = Track(name = "test", url="", artist = ""), subTitle =
    "sfhsjvbjdsabvujoeadbouvboujebaouvboua", onListingSelected = {},
            trailingText = "Vor 5 Minuten")

    //}
}