package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.ListingType
import de.schnettler.database.models.RelatedAlbum
import de.schnettler.database.models.RelatedArtist
import de.schnettler.database.models.RelatedTrack
import kotlinx.coroutines.flow.Flow

@Suppress("MaxLineLength")
@Dao
interface RelationshipDao {
    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedAlbums(
        id: String,
        sourceType: ListingType,
        targetType: ListingType = ListingType.ALBUM
    ): Flow<List<RelatedAlbum>>

    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedTracks(
        id: String,
        sourceType: ListingType,
        targetType: ListingType = ListingType.TRACK
    ): Flow<List<RelatedTrack>>

    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedArtists(
        id: String,
        sourceType: ListingType,
        targetType: ListingType = ListingType.ARTIST
    ): Flow<List<RelatedArtist>>
}