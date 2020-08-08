package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class LocalTrackDao : BaseDao<LocalTrack> {
    @Query("SELECT * FROM localTracks WHERE status != :exclude ORDER BY timestamp DESC LIMIT :limit")
    abstract fun getLocalTracks(
        exclude: ScrobbleStatus = ScrobbleStatus.PLAYING,
        limit: Int = 50
    ): Flow<List<LocalTrack>>

    @Query("SELECT COUNT(timestamp) FROM localTracks WHERE status = :status")
    abstract fun getNumberOfCachedScrobbles(status: ScrobbleStatus = ScrobbleStatus.LOCAL): Flow<Int>

    @Query("SELECT * FROM localTracks WHERE status = :include LIMIT 1")
    abstract fun getNowPlaying(include: ScrobbleStatus = ScrobbleStatus.PLAYING): Flow<LocalTrack?>

    @Query("SELECT * FROM localTracks WHERE status = :status ORDER BY timestamp DESC")
    abstract suspend fun getCachedTracks(status: ScrobbleStatus = ScrobbleStatus.LOCAL): List<LocalTrack>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTrack(track: LocalTrack): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrUpdatTrack(track: LocalTrack): Long

    @Query("UPDATE localTracks SET status = :status WHERE timestamp = :startTime AND playedBy = :packageName")
    abstract suspend fun updateTrackStatus(
        startTime: Long,
        packageName: String,
        status: ScrobbleStatus
    )

    @Query("SELECT * FROM localTracks ORDER BY timestamp DESC LIMIT 1")
    abstract fun getCurrentTrack(): Flow<LocalTrack>

    @Query("UPDATE localTracks SET album = :album WHERE timestamp = :startTime AND playedBy = :packageName")
    abstract suspend fun updateAlbum(album: String, startTime: Long, packageName: String)

    @Query("DELETE FROM localTracks WHERE status = :include")
    abstract fun deleteByStatus(include: ScrobbleStatus = ScrobbleStatus.PLAYING)

    @Query("UPDATE localTracks SET album = :album, artist = :artist, name = :track WHERE timestamp = :timestamp")
    abstract suspend fun updateTrackData(timestamp: Long, track: String, artist: String, album: String)
}