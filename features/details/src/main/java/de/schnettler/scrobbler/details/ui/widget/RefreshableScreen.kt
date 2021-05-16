package de.schnettler.scrobbler.details.ui.widget

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.widget.FullScreenError
import de.schnettler.scrobbler.compose.widget.LoadingContent
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState

@Composable
fun <Data> RefreshableScreen(
    state: RefreshableUiState<Data>,
    refresh: () -> Unit,
    errorer: @Composable (UIError) -> Unit,
    @StringRes errorId: Int,
    content: @Composable (Data) -> Unit
) {
    LoadingContent(
        empty = state.isInitialLoading,
        loading = state.isRefreshLoading,
        onRefresh = refresh,
        addStatusBarOffset = true,
    ) {
        state.currentData?.let { data ->
            content(data)
        } ?: FullScreenError()
    }

    if (state.isError) {
        errorer(
            UIError.ShowErrorSnackbar(
                state = state,
                fallbackMessage = stringResource(id = errorId),
                onAction = refresh
            )
        )
    }
}