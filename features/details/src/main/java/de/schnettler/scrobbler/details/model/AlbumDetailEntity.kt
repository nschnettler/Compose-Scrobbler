package de.schnettler.scrobbler.details.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import de.schnettler.scrobbler.ktx.sumByLong
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.EntityWithInfo
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Stats
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

data class AlbumDetailEntity(
    @Embedded val album: LastFmEntity.Album,
    @Relation(parentColumn = "id", entityColumn = "id") val stats: Stats?,
    @Relation(parentColumn = "id", entityColumn = "id") val info: EntityInfo?,
    @Relation(parentColumn = "artistId", entityColumn = "id") val artist: LastFmEntity.Artist?,
) {
    @Ignore
    var tracks: List<EntityWithInfo.TrackWithInfo> = listOf()

    @OptIn(ExperimentalTime::class)
    // TODO: Should not be here
    val runtime: Duration
        get() = tracks.sumByLong { it.info.durationInSeconds }.toDuration(DurationUnit.SECONDS)
    val trackNumber: Int
        get() = tracks.size
}