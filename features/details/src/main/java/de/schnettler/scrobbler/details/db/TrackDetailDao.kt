package de.schnettler.scrobbler.details.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class TrackDetailDao : BaseDao<LastFmEntity.Track> {
    @Transaction
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrackWithMetadata(id: String, artist: String): Flow<EntityWithStatsAndInfo.TrackWithStatsAndInfo?>

    @Query("UPDATE tracks SET album = :album, albumId = :albumId, imageUrl = :imageUrl WHERE id = :id")
    abstract fun updateAlbum(id: String, album: String?, albumId: String?, imageUrl: String?)

    @Transaction
    open suspend fun inserTrackOrUpdateMetadata(track: LastFmEntity.Track) {
        val result = insert(track)
        if (result == -1L) {
            updateAlbum(id = track.id, album = track.album, albumId = track.albumId, imageUrl = track.imageUrl)
        }
    }
}