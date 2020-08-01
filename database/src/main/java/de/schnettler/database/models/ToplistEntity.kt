package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Relation
import de.schnettler.database.models.LastFmEntity.Artist

data class TopListArtist(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: Artist
) : Toplist

data class TopListAlbum(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: LastFmEntity.Album
) : Toplist

data class TopListTrack(
    @Embedded override val listing: TopListEntry,
    @Relation(parentColumn = "id", entityColumn = "id") override val value: LastFmEntity.Track
) : Toplist