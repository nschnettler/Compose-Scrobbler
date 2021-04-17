package de.schnettler.scrobbler.ui.profile.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.theme.rememberDominantColorCache
import de.schnettler.scrobbler.compose.widget.Carousel
import de.schnettler.scrobbler.compose.widget.MediaCard
import de.schnettler.scrobbler.model.Toplist
import de.schnettler.scrobbler.ui.profile.R

@Composable
fun <T : Toplist> TopListCarousel(
    topList: List<T>?,
    @StringRes titleRes: Int? = null,
    spacing: Dp = 8.dp,
    itemSize: Dp = 160.dp,
    actionHandler: (UIAction) -> Unit,
) {
    val colorCache = rememberDominantColorCache()

    Carousel(
        items = topList,
        itemSpacing = spacing,
        titleRes = titleRes,
        action = {
            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary),
            ) {
                Text(text = stringResource(id = R.string.header_more))
            }
        }
    ) { toplist ->
        MediaCard(
            name = toplist.value.name,
            modifier = Modifier.size(itemSize),
            imageUrl = toplist.value.imageUrl,
            plays = toplist.listing.count,
            colorCache = colorCache
        ) { actionHandler(UIAction.ListingSelected(toplist.value)) }
    }
}