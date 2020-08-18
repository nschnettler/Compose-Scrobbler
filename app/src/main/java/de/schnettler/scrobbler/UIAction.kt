package de.schnettler.scrobbler

import de.schnettler.database.models.LastFmEntity

sealed class UIAction {
    class TagSelected(val id: String) : UIAction()
    class ListingSelected(val listing: LastFmEntity) : UIAction()
    class TrackLiked(val id: String, val liked: Boolean) : UIAction()
}