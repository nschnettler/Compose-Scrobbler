package de.schnettler.scrobbler.details.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.EntityWithStats
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Stats

data class ArtistDetailEntity(
    @Embedded val entity: LastFmEntity.Artist,
    @Relation(parentColumn = "id", entityColumn = "id") val stats: Stats?,
    @Relation(parentColumn = "id", entityColumn = "id") val info: EntityInfo?
) {
    @Ignore
    var similarArtists: List<LastFmEntity.Artist> = listOf()

    @Ignore
    var topAlbums: List<EntityWithStats.AlbumWithStats> = listOf()

    @Ignore
    var topTracks: List<EntityWithStats.TrackWithStats> = listOf()
}