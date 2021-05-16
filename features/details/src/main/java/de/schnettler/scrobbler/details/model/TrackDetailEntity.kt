package de.schnettler.scrobbler.details.model

import androidx.room.Embedded
import androidx.room.Relation
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Stats

data class TrackDetailEntity(
    @Embedded val track: LastFmEntity.Track,
    @Relation(parentColumn = "id", entityColumn = "id") val stats: Stats?,
    @Relation(parentColumn = "id", entityColumn = "id") val info: EntityInfo?,
    @Relation(parentColumn = "albumId", entityColumn = "id") val album: LastFmEntity.Album?
)