package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Track
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class TrackDao : BaseDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Track?

    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrackWithMetadata(id: String, artist: String): Flow<TrackWithStatsAndInfo?>

    @Query("SELECT * FROM tracks INNER JOIN stats ON tracks.id = stats.id WHERE tracks.artist = :artist ORDER BY stats.plays DESC LIMIT 5")
    abstract fun getTopTracksOfArtist(artist: String): Flow<List<TrackWithStats>>

    @Query("SELECT * FROM tracks WHERE artist = :artist and album = :album")
    abstract fun getTracksFromAlbum(artist: String, album: String): Flow<List<EntityWithInfo.TrackWithInfo>>

    @Query("UPDATE tracks SET album = :album, albumId = :albumId, imageUrl = :imageUrl WHERE id = :id")
    abstract fun updateAlbum(id: String, album: String?, albumId: String?, imageUrl: String?)

    @Query("UPDATE tracks SET imageUrl = :url WHERE id = :id")
    abstract suspend fun updateImageUrl(id: String, url: String): Int

    @Transaction
    open suspend fun inserTrackOrUpdateMetadata(track: Track) {
        val result = insert(track)
        if (result == -1L) {
            updateAlbum(id = track.id, album = track.album, albumId = track.albumId, imageUrl = track.imageUrl)
        }
    }
}