package de.schnettler.scrobbler.profile.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("MaxLineLength")
abstract class ToplistDao : BaseDao<TopListEntry> {
    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = 'ARTIST' AND listType = 'USER' ORDER BY `index` ASC")
    abstract fun getTopArtists(): Flow<List<TopListArtist>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = 'TRACK' AND listType = 'USER' ORDER BY `index` ASC")
    abstract fun getTopTracks(): Flow<List<TopListTrack>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = 'ALBUM' AND listType = 'USER' ORDER BY `index` ASC")
    abstract fun getTopAlbums(): Flow<List<TopListAlbum>>
}