package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Artist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao : BaseRelationsDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtist(id: String): Flow<Artist?>

    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    abstract fun getArtistImageUrl(id: String): String?

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    abstract fun updateArtistImageUrl(url: String, id: String): Int
}