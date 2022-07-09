package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao : BaseDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtist(id: String): Flow<Artist?>

    @Query("UPDATE artists SET spotifyId = :spotifyId WHERE id = :id")
    abstract suspend fun setSpotifyId(id: String, spotifyId: String): Int
}