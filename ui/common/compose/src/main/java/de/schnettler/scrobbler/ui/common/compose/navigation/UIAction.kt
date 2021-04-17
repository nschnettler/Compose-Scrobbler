package de.schnettler.scrobbler.ui.common.compose.navigation

import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState

sealed class UIAction {
    class TagSelected(val id: String) : UIAction()
    class ListingSelected(val listing: LastFmEntity) : UIAction()
    class OpenInBrowser(val url: String) : UIAction()
    object NavigateUp : UIAction()
    object OpenNotificationListenerSettings : UIAction()
}

sealed class UIError {
    class ShowErrorSnackbar(
        val state: RefreshableUiState<*>?,
        val fallbackMessage: String = "Unable to load data",
        val actionMessage: String? = "Refresh",
        val onAction: () -> Unit = { },
        val onDismiss: () -> Unit = { }
    ) : UIError()
    class Snackbar(
        val error: Throwable,
        val fallbackMessage: String = "Unable to load data",
        val actionMessage: String? = "Refresh",
        val onAction: () -> Unit = { },
        val onDismiss: () -> Unit = { }
    ) : UIError()

    class ScrobbleSubmissionResult(
        val accepted: Int,
        val ignored: Int,
        val actionMessage: String = "Details",
        val onAction: () -> Unit = { },
        val onDismiss: () -> Unit = { }
    ) : UIError()
}