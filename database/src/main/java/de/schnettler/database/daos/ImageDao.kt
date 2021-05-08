package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.LastFmEntity.Track
import de.schnettler.scrobbler.persistence.dao.BaseDao

@Suppress("MaxLineLength")
@Dao
abstract class ImageDao : BaseDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Track?

    @Query("UPDATE tracks SET imageUrl = :url WHERE id = :id")
    abstract suspend fun updateImageUrl(id: String, url: String): Int

    @Query("SELECT * FROM albums WHERE name = :name and artist = :artist")
    abstract fun getAlbumByName(name: String, artist: String): LastFmEntity.Album?

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    abstract suspend fun updateArtistImageUrl(id: String, url: String): Int

    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    abstract fun getArtistImageUrl(id: String): String?
}