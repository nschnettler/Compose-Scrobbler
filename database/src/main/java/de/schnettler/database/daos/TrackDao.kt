package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Track
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class TrackDao : BaseDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Flow<Track?>

    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrackWithMetadata(id: String, artist: String): Flow<TrackWithStatsAndInfo?>

    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract suspend fun getSingletTrack(id: String, artist: String): Track?

    @Query("SELECT * FROM tracks INNER JOIN stats ON tracks.id = stats.id WHERE tracks.artist = :artist ORDER BY stats.plays DESC LIMIT 5")
    abstract fun getTopTracksOfArtist(artist: String): Flow<List<TrackWithStats>>

    @Query("SELECT * FROM tracks WHERE artist = :artist and album = :album")
    abstract fun getTracksFromAlbum(artist: String, album: String): Flow<List<EntityWithInfo.TrackWithInfo>>
}