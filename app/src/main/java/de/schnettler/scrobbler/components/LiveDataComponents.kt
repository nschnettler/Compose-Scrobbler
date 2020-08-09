package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumnItems
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.util.formatter

@Composable
fun LiveDataLoadingComponent(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun GenericAdapterList(data: List<Toplist>, onListingSelected: (LastFmEntity) -> Unit) {
    LazyColumnItems(items = data) { item ->
        when (item) {
            is TopListArtist -> HistoryItem(
                listing = item.value,
                subTitle = "${formatter.format(item.listing.count)} Listener",
                onListingSelected = onListingSelected
            )
        }
        CustomDivider()
    }
}

@Composable
fun HistoryItem(
    listing: LastFmEntity,
    subTitle: String,
    onListingSelected: (LastFmEntity) -> Unit,
    trailingText: String? = null
) {
    ListItem(
        text = { Text(text = listing.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = { Text(text = subTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { NameListIcon(title = listing.name) },
        onClick = { onListingSelected.invoke(listing) },
        trailing = { trailingText?.let { Text(text = it) } }
    )
}

@Preview
@Composable
fun testPreview() {
    HistoryItem(
        listing = Track(name = "test", url = "", artist = ""), subTitle =
        "sfhsjvbjdsabvujoeadbouvboujebaouvboua", onListingSelected = {},
        trailingText = "Vor 5 Minuten"
    )
}