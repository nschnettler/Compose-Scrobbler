package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.LastFmEntity.Track

@Dao
abstract class TrackDao : BaseDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Track?
}