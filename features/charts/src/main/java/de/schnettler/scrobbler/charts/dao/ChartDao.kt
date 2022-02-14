package de.schnettler.scrobbler.charts.dao

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.persistence.dao.BaseDao

@Dao
@Suppress("MaxLineLength")
abstract class ChartDao : BaseDao<TopListEntry> {
//    @Transaction
//    @Query("SELECT * FROM toplist WHERE entityType = 'ARTIST' AND listType = 'CHART' ORDER BY `index` ASC")
//    abstract fun getTopArtists(): PagingSource<Int, TopListArtist>

//    @Transaction
//    @Query("SELECT * FROM toplist WHERE entityType = 'TRACK' AND listType = 'CHART' ORDER BY `index` ASC")
//    abstract fun getTopTracks(): PagingSource<Int, TopListTrack>

    @Query("DELETE FROM toplist WHERE entityType = 'ARTIST' AND listType = 'CHART'")
    abstract suspend fun clearTopArtists()

    @Query("DELETE FROM toplist WHERE entityType = 'TRACK' AND listType = 'CHART'")
    abstract suspend fun clearTopTracks()
}