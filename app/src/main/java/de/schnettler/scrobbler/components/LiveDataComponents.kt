package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.formatter

@Composable
fun LiveDataLoadingComponent(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator(
            color = colorResource(id = R.color.colorAccent),
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun GenericAdapterList(data: List<CommonEntity>, onListingSelected: (CommonEntity) -> Unit) {
    LazyColumnItems(items = data) { item ->
        when (item) {
            is Artist -> HistoryItem(
                listing = item,
                subTitle = "${formatter.format(item.listeners)} Listener ⦁ ${formatter.format(item.plays)} Plays",
                onListingSelected = onListingSelected
            )
        }
        Divider(color = colorResource(id = R.color.colorStroke))
    }
}

@Composable
fun HistoryItem(
    listing: CommonEntity,
    subTitle: String,
    onListingSelected: (CommonEntity) -> Unit,
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