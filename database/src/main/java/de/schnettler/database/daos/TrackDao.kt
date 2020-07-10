package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Track
import de.schnettler.database.models.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrackDao: BaseRelationsDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Flow<TrackWithAlbum?>

    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract suspend fun getSingletTrack(id: String, artist: String): Track?

    @Query("SELECT imageUrl FROM tracks WHERE id = :id")
    abstract fun getTrackImageUrl(id: String): String?

    @Query("UPDATE tracks SET imageUrl = :url WHERE id = :id")
    abstract fun updateImageUrl(url: String, id: String): Int

    @Query("UPDATE tracks SET plays = :plays, listeners = :listeners WHERE id = :trackId and artist = :artistId")
    abstract fun updateStats(plays: Long, listeners: Long, trackId: String, artistId: String)

    @Query("SELECT * FROM tracks WHERE album = :album and artist = :artist ORDER BY rank ASC")
    abstract fun getAlbumTracks(album: String, artist: String): Flow<List<Track>>

    suspend fun insertOrUpdateStats(tracks: List<Track>) {
        val result = insertAll(tracks)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                val track = tracks[index]
                updateStats(track.plays, track.listeners, track.id, track.artist)
            }
        }
    }
}