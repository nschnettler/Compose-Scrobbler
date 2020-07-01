package de.schnettler.database.daos

import androidx.room.*
import de.schnettler.database.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseDao<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: T): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun forceInsert(obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(obj: List<T>): List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun forceInsertAll(obj: List<T>): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: T)

    @Update
    fun updateAll(obj: List<T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)


    @Transaction
    fun upsert(obj: T) {
        val id: Long = insert(obj)
        if (id == -1L) {
            //Get old value
            update(obj)
        }
    }

   @Transaction
   fun upsertAll(objList: List<@JvmSuppressWildcards T>) {
       val insertResult: List<Long> = insertAll(objList)
       val updateList = objList.filterIndexed { index, value ->  insertResult[index] == -1L}
       if (updateList.isNotEmpty()) {
           updateAll(updateList)
       }
   }
}

@Dao
interface BaseRelationsDao<T>: BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    suspend fun insertEntriesWithRelations(entities: List<@JvmSuppressWildcards T>, relations: List<RelationEntity>) {
        insertAll(entities)
        insertRelations(relations)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopListEntries(topListEntries: List<TopListEntry>)

    @Transaction
    suspend fun insertEntitiesWithTopListEntries(entities: List<@JvmSuppressWildcards T>, topListEntries: List<TopListEntry>) {
        insertAll(entities)
        insertTopListEntries(topListEntries)
    }
}

@Dao
abstract class AuthDao: BaseDao<AuthToken> {
    @Query("SELECT * FROM sessions LIMIT 1")
    abstract fun getSession(): Flow<Session?>

    @Query("SELECT * FROM sessions LIMIT 1")
    abstract suspend fun getSessionOnce(): Session?

    @Insert
    abstract suspend fun insertSession(session: Session)

    @Delete
    abstract suspend fun deleteSession(session: Session)


    /*
    Spotify
     */
    @Query("DELETE FROM auth WHERE tokenType = :type")
    abstract suspend fun deleteAuthToken(type: String)

    @Query("SELECT * FROM auth WHERE tokenType = :type")
    abstract fun getAuthToken(type: String): Flow<AuthToken?>
}


@Dao
abstract class ChartDao {
    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopArtists(type: TopListEntryType): Flow<List<TopListArtist>>

    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopTracks(type: TopListEntryType): Flow<List<TopListTrack>>

    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    abstract fun getTopAlbums(type: TopListEntryType): Flow<List<TopListAlbum>>
}

@Dao
abstract class ArtistDao: BaseRelationsDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getArtist(id: String): Flow<Artist?>

    @Query("SELECT imageUrl FROM artists WHERE id = :id")
    abstract fun getArtistImageUrl(id: String): String?

    @Query("UPDATE artists SET imageUrl = :url WHERE id = :id")
    abstract fun updateArtistImageUrl(url: String, id: String): Int
}

@Dao
abstract class AlbumDao: BaseRelationsDao<Album> {
    @Query("SELECT * FROM albums WHERE id = :id and artist = :artistId")
    abstract fun getAlbum(id: String, artistId: String): Flow<Album?>

    @Query("SELECT imageUrl FROM albums WHERE id = :id")
    abstract fun getImageUrl(id: String): String?

    @Query("UPDATE albums SET imageUrl = :url WHERE id = :albumId and artist = :artistId")
    abstract fun updateImageUrl(url: String, albumId: String, artistId: String)

    @Query("UPDATE albums SET plays = :plays WHERE id = :albumId and artist = :artistId")
    abstract fun updatePlays(plays: Long, albumId: String, artistId: String)

    fun insertOrUpdateStats(albums: List<Album>) {
        val result = insertAll(albums)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                val album = albums[index]
                updatePlays(album.plays, album.id, album.artist!!)
            }
        }
    }
}

@Dao
abstract class TrackDao: BaseRelationsDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id and artist = :artist")
    abstract fun getTrack(id: String, artist: String): Flow<TrackWithAlbum?>

    @Query("SELECT imageUrl FROM tracks WHERE id = :id")
    abstract fun getTrackImageUrl(id: String): String?

    @Query("UPDATE tracks SET plays = :plays, listeners = :listeners WHERE id = :trackId and artist = :artistId")
    abstract fun updateStats(plays: Long, listeners: Long, trackId: String, artistId: String)

    fun insertOrUpdateStats(tracks: List<Track>) {
        val result = insertAll(tracks)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                val track = tracks[index]
                updateStats(track.plays, track.listeners, track.id, track.artist)
            }
        }
    }
}

@Dao
abstract class RelationshipDao {
    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    abstract fun getRelatedAlbums(id: String, sourceType: ListingType, targetType: ListingType = ListingType.ALBUM): Flow<List<RelatedAlbum>>

    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    abstract fun getRelatedTracks(id: String, sourceType: ListingType, targetType: ListingType = ListingType.TRACK): Flow<List<RelatedTrack>>

    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    abstract fun getRelatedArtists(id: String, sourceType: ListingType, targetType: ListingType = ListingType.ARTIST): Flow<List<RelatedArtist>>
}

@Dao
abstract class UserDao: BaseDao<User> {
    @Query("SELECT * FROM users WHERE name = :name")
    abstract fun getUser(name: String): Flow<User?>

    @Query("SELECT * FROM users WHERE name = :name")
    abstract fun getUserOnce(name: String): User?

    @Query("UPDATE users SET artistCount = :count WHERE name = :userName")
    abstract suspend fun updateArtistCount(userName: String, count: Long): Int

    @Query("UPDATE users SET lovedTracksCount = :count WHERE name = :userName")
    abstract suspend fun updateLovedTracksCount(userName: String, count: Long)
}