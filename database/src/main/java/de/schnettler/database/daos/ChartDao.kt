package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface ChartDao {
    @Query("SELECT * FROM toplist WHERE entityType = :entityType AND listType = :listType ORDER BY `index` ASC")
    fun getTopArtists(entityType: EntityType = EntityType.ARTIST, listType: ListType): Flow<List<TopListArtist>>

    @Query("SELECT * FROM toplist WHERE entityType = :type ORDER BY `index` ASC")
    fun getTopTracks(type: EntityType = EntityType.TRACK): Flow<List<TopListTrack>>

    @Query("SELECT * FROM toplist WHERE entityType = :type ORDER BY `index` ASC")
    fun getTopAlbums(type: EntityType = EntityType.ALBUM): Flow<List<TopListAlbum>>
}