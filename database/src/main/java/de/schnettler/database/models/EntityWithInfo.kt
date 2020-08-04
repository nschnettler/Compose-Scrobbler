package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.sumByLong
import java.util.concurrent.TimeUnit

sealed class EntityWithInfo(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val info: EntityInfo
) : BaseEntity {

    data class AlbumWithInfo(
        @Embedded override val entity: LastFmEntity.Album,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo
    ) : EntityWithInfo(entity, info) {
        @Ignore var tracks: List<TrackWithStatsAndInfo> = listOf()
        fun getLength() = TimeUnit.SECONDS.toMinutes(tracks.sumByLong { it.info?.duration ?: 0 })
    }

    data class ArtistWithInfo(
        override val entity: LastFmEntity.Artist,
        override val info: EntityInfo
    ) : EntityWithInfo(entity, info)

    data class TrackWithInfo(
        @Embedded override val entity: LastFmEntity.Track,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo
    ) : EntityWithInfo(entity, info)
}