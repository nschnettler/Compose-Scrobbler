package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListEntryType
import de.schnettler.database.models.TopListTrack
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChartDao {
    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopArtists(type: TopListEntryType): Flow<List<TopListArtist>>

    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopTracks(type: TopListEntryType): Flow<List<TopListTrack>>

    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopAlbums(type: TopListEntryType): Flow<List<TopListAlbum>>
}