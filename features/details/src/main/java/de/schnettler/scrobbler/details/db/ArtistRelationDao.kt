package de.schnettler.scrobbler.details.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.details.model.RelatedArtist
import de.schnettler.scrobbler.details.model.RelatedArtistEntry
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistRelationDao : BaseDao<RelatedArtistEntry> {
    @Transaction
    @Query("SELECT * FROM artist_relations WHERE artistId = :id ORDER BY `orderIndex` ASC")
    abstract fun getRelatedArtists(id: String): Flow<List<RelatedArtist>>
}