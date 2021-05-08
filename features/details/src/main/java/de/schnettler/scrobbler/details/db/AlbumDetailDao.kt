package de.schnettler.scrobbler.details.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.EntityWithInfo
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo
import de.schnettler.scrobbler.model.LastFmEntity.Album
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class AlbumDetailDao : BaseDao<Album> {
    @Transaction
    @Query("SELECT * FROM albums WHERE id = :id and artist = :artist")
    abstract fun getAlbumWithStatsAndInfo(id: String, artist: String): Flow<EntityWithStatsAndInfo.AlbumDetails?>

    @Transaction
    @Query("SELECT * FROM tracks WHERE artist = :artist and album = :album")
    abstract fun getTracksFromAlbum(artist: String, album: String): Flow<List<EntityWithInfo.TrackWithInfo>>
}