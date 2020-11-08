package de.schnettler.scrobbler.screens

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.components.LoadingScreen
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.util.RefreshableUiState

@Composable
fun <Data> RefreshableScreen(
    state: RefreshableUiState<Data>,
    refresh: () -> Unit,
    errorer: @Composable (UIError) -> Unit,
    @StringRes errorId: Int,
    content: @Composable (Data) -> Unit
) {
    if (state.isError) {
        errorer(
            UIError.ShowErrorSnackbar(
                state = state,
                fallbackMessage = stringResource(id = errorId),
                onAction = refresh
            )
        )
    }

    if (state.isLoading) { LoadingScreen() } else {
        SwipeToRefreshLayout(
            refreshingState = state.isRefreshing,
            onRefresh = refresh,
        ) {
            state.currentData?.let { data ->
                content(data)
            }
        }
    }
}