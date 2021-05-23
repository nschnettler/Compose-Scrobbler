package de.schnettler.scrobbler.image.db

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.LastFmEntity

@Dao
@Suppress("MaxLineLength")
interface ImageDao {
    @Query("SELECT * FROM artists INNER JOIN toplist ON artists.id = toplist.id WHERE listType = 'USER' AND imageUrl is NULL ")
    suspend fun getTopArtistsWithoutImages(): List<LastFmEntity.Artist>

    @Query("SELECT * FROM tracks INNER JOIN toplist ON tracks.id = toplist.id WHERE listType = 'USER' AND imageUrl is NULL ")
    suspend fun getTopTracksWithoutImages(): List<LastFmEntity.Track>

    @Query("UPDATE tracks SET imageUrl = :url WHERE id = :id")
    suspend fun updateTrackImageUrl(id: String, url: String): Int

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    suspend fun updateArtistImageUrl(id: String, url: String): Int

    // Testing
    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    suspend fun getArtistImageUrl(id: String): String?
}