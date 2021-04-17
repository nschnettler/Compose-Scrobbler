package de.schnettler.scrobbler.model

import androidx.room.Embedded
import androidx.room.Relation
import de.schnettler.scrobbler.model.LastFmEntity.Album
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import de.schnettler.scrobbler.model.LastFmEntity.Track

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