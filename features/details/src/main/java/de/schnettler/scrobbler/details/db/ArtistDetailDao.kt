package de.schnettler.scrobbler.details.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.scrobbler.model.EntityWithStats
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("MaxLineLength")
abstract class ArtistDetailDao : BaseDao<Artist> {
    @Transaction
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtistDetails(id: String): Flow<EntityWithStatsAndInfo.ArtistWithStatsAndInfo?>

    @Transaction
    @Query("SELECT * FROM albums INNER JOIN stats ON albums.id = stats.id WHERE albums.artist = :artist ORDER BY stats.plays DESC LIMIT 5")
    abstract fun getTopAlbumsOfArtist(artist: String): Flow<List<EntityWithStats.AlbumWithStats>>

    @Transaction
    @Query("SELECT * FROM tracks INNER JOIN stats ON tracks.id = stats.id WHERE tracks.artist = :artist ORDER BY stats.plays DESC LIMIT 5")
    abstract fun getTopTracksOfArtist(artist: String): Flow<List<EntityWithStats.TrackWithStats>>
}