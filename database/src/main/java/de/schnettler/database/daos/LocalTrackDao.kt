package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength", "TooManyFunctions")
@Dao
abstract class LocalTrackDao : BaseDao<Scrobble> {
    @Query("SELECT * FROM localTracks ORDER BY timestamp DESC LIMIT :limit")
    abstract fun getListeningHistory(
        limit: Int = 50
    ): Flow<List<Scrobble>>

    @Query("SELECT COUNT(timestamp) FROM localTracks WHERE status = :status")
    abstract fun getNumberOfCachedScrobbles(status: ScrobbleStatus = ScrobbleStatus.LOCAL): Flow<Int>

    @Query("SELECT * FROM localTracks WHERE status = :include LIMIT 1")
    abstract fun getNowPlaying(include: ScrobbleStatus = ScrobbleStatus.PLAYING): Flow<Scrobble?>

    @Query("SELECT * FROM localTracks WHERE status = :status ORDER BY timestamp DESC")
    abstract suspend fun getCachedTracks(status: ScrobbleStatus = ScrobbleStatus.LOCAL): List<Scrobble>

    @Query("DELETE FROM localTracks WHERE status = :include")
    abstract fun deleteByStatus(include: ScrobbleStatus = ScrobbleStatus.PLAYING)

    @Query("UPDATE localTracks SET album = :album, artist = :artist, name = :track WHERE timestamp = :timestamp")
    abstract suspend fun updateTrackData(timestamp: Long, track: String, artist: String, album: String)

    @Query("UPDATE localTracks SET status = :status WHERE timestamp in (:timestamps)")
    abstract suspend fun updateScrobbleStatus(timestamps: List<Long>, status: ScrobbleStatus = ScrobbleStatus.SCROBBLED)

    @Query("SELECT * FROM localTracks WHERE timestamp in (:timestamps)")
    abstract suspend fun getScrobblesWithTimestamp(timestamps: List<Long>): List<Scrobble>
}