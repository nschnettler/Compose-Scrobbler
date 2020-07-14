package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocalTrackDao:BaseDao<LocalTrack> {
    @Query("SELECT * FROM localTracks ORDER BY timestamp DESC")
    abstract fun getLocalTracks(): Flow<List<LocalTrack>>

    @Query("SELECT * FROM localTracks WHERE status = :status ORDER BY timestamp DESC")
    abstract suspend fun getCachedTracks(status: ScrobbleStatus = ScrobbleStatus.LOCAL): List<LocalTrack>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTrack(track: LocalTrack): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrUpdatTrack(track: LocalTrack): Long

    @Query("UPDATE localTracks SET status = :status WHERE timestamp = :startTime AND playedBy = :packageName")
    abstract suspend fun updateTrackStatus(startTime: Long, packageName: String, status: ScrobbleStatus)

    @Query("SELECT * FROM localTracks ORDER BY timestamp DESC LIMIT 1")
    abstract fun getCurrentTrack(): Flow<LocalTrack>

    @Query("UPDATE localTracks SET album = :album WHERE timestamp = :startTime AND playedBy = :packageName")
    abstract suspend fun updateAlbum(album: String, startTime: Long, packageName: String)
}