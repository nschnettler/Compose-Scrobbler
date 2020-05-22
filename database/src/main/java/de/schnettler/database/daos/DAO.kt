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
    fun insert(obj: T)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun forceInsert(obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(obj: List<T>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun forceInsertAll(obj: List<T>)

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: T)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)
}

@Dao
interface AuthDao: BaseDao<AuthToken> {
    @Query("SELECT * FROM sessions LIMIT 1")
    fun getSession(): Flow<Session?>

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)


    /*
    Spotify
     */
    @Query("DELETE FROM auth WHERE tokenType = :type")
    suspend fun deleteAuthToken(type: String)

    @Query("SELECT * FROM auth WHERE tokenType = :type")
    fun getAuthToken(type: String): Flow<AuthToken?>
}


@Dao
interface ChartDao {
    @Transaction
    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: String): Flow<List<ListEntryWithArtist>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(entries: List<ListEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artist: List<Artist?>)

    @Transaction
    suspend fun insertTopArtists(artistEntry: List<ListEntryWithArtist>) {
        insertTopList(artistEntry.map { it.listing })
        insertArtists(artistEntry.map { it.artist })
    }
}

@Dao
interface ArtistDao: BaseDao<Artist> {
    @Query("SELECT * FROM artists WHERE id = :id")
    fun getArtist(id: String): Flow<Artist?>
}

@Dao
interface AlbumDao: BaseDao<Album> {
    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbum(id: String): Flow<Album?>
}

@Dao
interface TrackDao: BaseDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id")
    fun getTrack(id: String): Flow<Track?>
}

@Dao
interface RelationshipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedAlbums(id: String, sourceType: ListingType, targetType: ListingType = ListingType.ALBUM): Flow<List<RelatedAlbum>>

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedTracks(id: String, sourceType: ListingType, targetType: ListingType = ListingType.TRACK): Flow<List<RelatedTrack>>

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceId = :id AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedArtists(id: String, sourceType: ListingType, targetType: ListingType = ListingType.ARTIST): Flow<List<RelatedArtist>>
}

@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM charts WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: String): Flow<List<ListEntryWithArtist>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(entries: List<ListEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artist: List<Artist?>)

    @Transaction
    suspend fun insertTopArtists(artistEntry: List<ListEntryWithArtist>) {
        insertTopList(artistEntry.map { it.listing })
        insertArtists(artistEntry.map { it.artist })
    }
}