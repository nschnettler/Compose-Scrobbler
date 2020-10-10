package de.schnettler.scrobbler.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.util.RefreshableUiState

@Composable
fun <T> Carousel(
    items: List<T>?,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    @StringRes titleRes: Int? = null,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
    itemSpacing: Dp = 8.dp,
    verticalGravity: Alignment.Vertical = Alignment.Top,
    action: @Composable () -> Unit = { },
    itemContent: @Composable LazyItemScope.(T, PaddingValues) -> Unit
) {
    val halfSpacing = itemSpacing / 2
    val spacingContent = PaddingValues(halfSpacing, 0.dp, halfSpacing, 0.dp)

    titleRes?.let {
        Header(
            title = stringResource(id = titleRes),
            loading = loading,
            action = action,
            modifier = Modifier.fillMaxWidth()
        )
    }

    LazyRowFor(
        items = items ?: emptyList(),
        modifier = modifier,
        contentPadding = contentPadding.copy(
            start = (contentPadding.start - halfSpacing).coerceAtLeast(0.dp),
            end = (contentPadding.end - halfSpacing).coerceAtLeast(0.dp)
        ),
        verticalAlignment = verticalGravity,
        itemContent = { item -> itemContent(item, spacingContent) }
    )
}

@Composable
fun <T : Toplist> TopListCarousel(
    state: RefreshableUiState<List<T>>,
    @StringRes titleRes: Int? = null,
    spacing: Dp = 8.dp,
    itemSize: Dp = 200.dp,
    actionHandler: (UIAction) -> Unit,
) {
    val colorCache = rememberDominantColorCache()

    Carousel(
        items = state.currentData,
        itemSpacing = spacing,
        titleRes = titleRes,
        loading = state.isLoading,
        action = {
            TextButton(
                onClick = { },
                contentColor = MaterialTheme.colors.secondary,
            ) {
                Text(text = stringResource(id = R.string.header_more))
            }
        }
    ) { toplist, padding ->
        MediaCard(
            name = toplist.value.name,
            modifier = Modifier.padding(padding).preferredSize(itemSize),
            imageUrl = toplist.value.imageUrl,
            plays = toplist.listing.count,
            colorCache = colorCache
        ) { actionHandler(UIAction.ListingSelected(toplist.value)) }
    }
}