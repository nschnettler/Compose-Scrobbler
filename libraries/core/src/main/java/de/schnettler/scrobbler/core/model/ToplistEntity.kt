package de.schnettler.scrobbler.core.model

import androidx.room.Embedded
import androidx.room.Relation
import de.schnettler.scrobbler.core.model.LastFmEntity.Album
import de.schnettler.scrobbler.core.model.LastFmEntity.Artist
import de.schnettler.scrobbler.core.model.LastFmEntity.Track

data class TopListArtist(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: Artist
) : Toplist

data class TopListAlbum(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: Album
) : Toplist

data class TopListTrack(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: Track
) : Toplist