package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface ChartDao {
    @Query("SELECT * FROM toplist WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: EntityType = EntityType.ARTIST): Flow<List<TopListArtist>>

    @Query("SELECT * FROM toplist WHERE type = :type ORDER BY `index` ASC")
    fun getTopTracks(type: EntityType = EntityType.TRACK): Flow<List<TopListTrack>>

    @Query("SELECT * FROM toplist WHERE type = :type ORDER BY `index` ASC")
    fun getTopAlbums(type: EntityType = EntityType.ALBUM): Flow<List<TopListAlbum>>
}