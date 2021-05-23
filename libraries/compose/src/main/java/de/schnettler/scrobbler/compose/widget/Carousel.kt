package de.schnettler.scrobbler.compose.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> Carousel(
    items: List<T>?,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    @StringRes titleRes: Int? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    itemSpacing: Dp = 8.dp,
    verticalGravity: Alignment.Vertical = Alignment.Top,
    action: @Composable () -> Unit = { },
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    titleRes?.let {
        Header(
            title = stringResource(id = titleRes),
            loading = loading,
            action = action,
            modifier = Modifier.fillMaxWidth()
        )
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        contentPadding = contentPadding,
        verticalAlignment = verticalGravity
    ) {
        items(items = items ?: emptyList()) { item ->
            itemContent(item)
        }
    }
}