package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.core.model.LastFmEntity.Artist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao : BaseDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtist(id: String): Flow<Artist?>

    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtistWithMetadata(id: String): Flow<ArtistWithStatsAndInfo?>

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    abstract suspend fun updateArtistImageUrl(id: String, url: String): Int

    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    abstract fun getArtistImageUrl(id: String): String?
}