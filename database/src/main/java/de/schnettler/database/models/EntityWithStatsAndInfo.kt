package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

sealed class EntityWithStatsAndInfo(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val stats: Stats,
    @Ignore open val info: EntityInfo
) : BaseEntity {

    data class AlbumWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Album,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo,
    ) : EntityWithStatsAndInfo(entity, stats, info)

    data class ArtistWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Artist,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo
    ) : EntityWithStatsAndInfo(entity, stats, info) {
        @Ignore
        var similarArtists: List<LastFmEntity.Artist> = listOf()
        @Ignore
        var topAlbums: List<EntityWithStats.AlbumWithStats> = listOf()
        @Ignore
        var topTracks: List<EntityWithStats.TrackWithStats> = listOf()
    }

    data class TrackWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Track,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo,
    ) : EntityWithStatsAndInfo(entity, stats, info)
}