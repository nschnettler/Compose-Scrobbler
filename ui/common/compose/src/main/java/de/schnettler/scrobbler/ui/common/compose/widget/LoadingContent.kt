package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.runtime.Composable
import de.schnettler.scrobbler.ui.common.compose.SwipeToRefreshLayout

@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = onRefresh,
            content = content,
        )
    }
}