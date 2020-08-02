package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import de.schnettler.database.models.EntityWithInfo.TrackWithInfo
import de.schnettler.database.sumByLong
import java.util.concurrent.TimeUnit

sealed class EntityWithStatsAndInfo(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val stats: Stats,
    @Ignore open val info: EntityInfo,
    @Ignore open val image: EntityInfo?
) : BaseEntity {

    data class AlbumWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Album,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo,
        @Relation(parentColumn = "id", entityColumn = "id") override val image: EntityInfo? = null,
        //  @Relation(parentColumn = "id", entityColumn = "album_id") val tracks: Track
    ) : EntityWithStatsAndInfo(entity, stats, info, image) {
        @Ignore var tracks: List<TrackWithInfo> = listOf()
        fun getLength() = TimeUnit.SECONDS.toMinutes(tracks.sumByLong { it.info.duration })
    }

    data class ArtistWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Artist,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo,
        @Relation(parentColumn = "id", entityColumn = "id") override val image: EntityInfo? = null
    ) : EntityWithStatsAndInfo(entity, stats, info, image) {
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
        @Relation(parentColumn = "id", entityColumn = "id") override val image: EntityInfo? = null
    ) : EntityWithStatsAndInfo(entity, stats, info, image)
}