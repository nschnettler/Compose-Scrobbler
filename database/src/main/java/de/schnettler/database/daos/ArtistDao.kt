package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.RelatedArtist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao : BaseDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtist(id: String): Flow<Artist?>

    @Query("SELECT * FROM artist_relations WHERE artistId = :id ORDER BY `orderIndex` ASC")
    abstract fun getRelatedArtists(
        id: String
    ): Flow<List<RelatedArtist>>

    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtistWithMetadata(id: String): Flow<ArtistWithStatsAndInfo?>

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    abstract suspend fun updateArtistImageUrl(id: String, url: String): Int

    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    abstract fun getArtistImageUrl(id: String): String?
}