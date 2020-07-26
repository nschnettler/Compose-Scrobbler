package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Album
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao : BaseRelationsDao<Album> {
    @Query("SELECT * FROM albums WHERE id = :id and artist = :artistId")
    abstract fun getAlbum(id: String, artistId: String): Flow<Album?>

    @Query("SELECT imageUrl FROM albums WHERE id = :id")
    abstract fun getImageUrl(id: String): String?

    @Query("UPDATE albums SET imageUrl = :url WHERE id = :albumId and artist = :artistId")
    abstract fun updateImageUrl(url: String, albumId: String, artistId: String)

    @Query("UPDATE albums SET plays = :plays WHERE id = :albumId and artist = :artistId")
    abstract fun updatePlays(plays: Long, albumId: String, artistId: String)

    suspend fun insertOrUpdateStats(albums: List<Album>) {
        val result = insertAll(albums)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                val album = albums[index]
                updatePlays(album.plays, album.id, album.artist!!)
            }
        }
    }
}