package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("MaxLineLength")
abstract class ChartDao : BaseDao<TopListEntry> {
    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :entityType AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopArtists(entityType: EntityType = EntityType.ARTIST, listType: ListType): Flow<List<TopListArtist>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :type AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopTracks(type: EntityType = EntityType.TRACK, listType: ListType = ListType.USER): Flow<List<TopListTrack>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :type AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopAlbums(type: EntityType = EntityType.ALBUM, listType: ListType = ListType.USER): Flow<List<TopListAlbum>>
}