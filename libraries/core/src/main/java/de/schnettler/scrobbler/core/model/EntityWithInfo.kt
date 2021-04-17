package de.schnettler.scrobbler.core.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

sealed class EntityWithInfo(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val info: EntityInfo
) : BaseEntity {

    data class TrackWithInfo(
        @Embedded override val entity: LastFmEntity.Track,
        @Relation(parentColumn = "id", entityColumn = "id") override val info: EntityInfo
    ) : EntityWithInfo(entity, info)
}