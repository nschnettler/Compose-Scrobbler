package de.schnettler.scrobbler

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.LastFmEntity

sealed class UIAction {
    class TagSelected(val id: String) : UIAction()
    class ListingSelected(val listing: LastFmEntity) : UIAction()
    class TrackLiked(val track: LastFmEntity.Track, val info: EntityInfo) : UIAction()
}