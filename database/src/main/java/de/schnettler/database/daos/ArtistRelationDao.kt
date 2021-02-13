package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.database.models.RelatedArtist
import de.schnettler.database.models.RelatedArtistEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistRelationDao : BaseDao<RelatedArtistEntry> {
    @Transaction
    @Query("SELECT * FROM artist_relations WHERE artistId = :id ORDER BY `orderIndex` ASC")
    abstract fun getRelatedArtists(
        id: String
    ): Flow<List<RelatedArtist>>
}