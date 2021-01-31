package de.schnettler.scrobbler.ui.common.compose.navigation

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState

sealed class UIAction {
    class TagSelected(val id: String) : UIAction()
    class ListingSelected(val listing: LastFmEntity) : UIAction()
    class TrackLiked(val track: LastFmEntity.Track, val info: EntityInfo) : UIAction()
    class OpenInBrowser(val url: String) : UIAction()
    object NavigateUp : UIAction()
    object ShowTimePeriodDialog : UIAction()
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
}