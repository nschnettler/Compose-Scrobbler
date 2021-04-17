package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.EntityWithStats.AlbumWithStats
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.scrobbler.model.LastFmEntity.Album
import de.schnettler.scrobbler.persistence.BaseDao
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
abstract class AlbumDao : BaseDao<Album> {
    @Query("SELECT * FROM albums WHERE name = :name and artist = :artist")
    abstract fun getAlbumByName(name: String, artist: String): Album?

    @Query("SELECT * FROM albums INNER JOIN stats ON albums.id = stats.id WHERE albums.artist = :artist ORDER BY stats.plays DESC LIMIT 5")
    abstract fun getTopAlbumsOfArtist(artist: String): Flow<List<AlbumWithStats>>

    @Query("SELECT * FROM albums WHERE id = :id and artist = :artist")
    abstract fun getAlbumWithStatsAndInfo(id: String, artist: String): Flow<AlbumDetails?>
}