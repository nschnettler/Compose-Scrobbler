package de.schnettler.scrobbler.core.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import de.schnettler.scrobbler.core.ktx.sumByLong
import de.schnettler.scrobbler.core.model.EntityWithInfo.TrackWithInfo
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

sealed class EntityWithStatsAndInfo(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val stats: Stats?,
    @Ignore open val info: EntityInfo?,
) : BaseEntity {

    data class AlbumDetails(
        @Embedded override val entity: LastFmEntity.Album,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats?,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo?,
        @Relation(parentColumn = "artistId", entityColumn = "id") val artist: LastFmEntity.Artist?,
    ) : EntityWithStatsAndInfo(entity, stats, info) {
        @Ignore var tracks: List<TrackWithInfo> = listOf()
        @OptIn(ExperimentalTime::class)
        val runtime: kotlin.time.Duration
            get() = tracks.sumByLong { it.info.durationInSeconds }.toDuration(DurationUnit.SECONDS)
        val trackNumber: Int
            get() = tracks.size
    }

    data class ArtistWithStatsAndInfo(
        @Embedded override val entity: LastFmEntity.Artist,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats?,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo?
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
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats?,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo?,
        @Relation(parentColumn = "albumId", entityColumn = "id") val album: LastFmEntity.Album?
    ) : EntityWithStatsAndInfo(entity, stats, info)
}