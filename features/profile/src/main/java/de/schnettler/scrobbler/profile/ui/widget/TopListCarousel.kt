package de.schnettler.scrobbler.profile.ui.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.theme.rememberDominantColorState
import de.schnettler.scrobbler.compose.widget.Carousel
import de.schnettler.scrobbler.compose.widget.MediaCard
import de.schnettler.scrobbler.model.Toplist
import de.schnettler.scrobbler.profile.R

@Composable
fun <T : Toplist> TopListCarousel(
    topList: List<T>?,
    @StringRes titleRes: Int? = null,
    spacing: Dp = 8.dp,
    itemSize: Dp = 160.dp,
    actionHandler: (UIAction) -> Unit,
) {
    val colorState = rememberDominantColorState()

    Carousel(
        items = topList,
        itemSpacing = spacing,
        titleRes = titleRes,
        action = {
            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
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
            colorState = colorState
        ) { actionHandler(UIAction.ListingSelected(toplist.value)) }
    }
}