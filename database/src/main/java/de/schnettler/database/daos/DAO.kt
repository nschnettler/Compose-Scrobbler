package de.schnettler.database.daos

import androidx.room.*
import de.schnettler.database.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM sessions LIMIT 1")
    fun getSession(): Flow<Session?>

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)


    /*
    Spotify
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthToken(authTokenDB: AuthToken)

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
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(albums: List<Artist>)

    @Query("SELECT * FROM artists WHERE name = :name")
    fun getArtist(name: String): Flow<Artist?>

//    @Query("SELECT name, plays, imageUrl FROM artists WHERE name = :name")
//    suspend fun getArtistMinimal(name: String): MinimalEntity
}

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)

    @Query("SELECT * FROM albums WHERE name = :name")
    fun getAlbum(name: String): Flow<Album?>

//    @Query("SELECT name, plays, listeners, imageUrl FROM album WHERE name = :name")
//    suspend fun getAlbumMinimal(name: String): MinimalEntity
}

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(albums: List<Track>)

    @Query("SELECT * FROM tracks WHERE name = :name")
    fun getTrack(name: String): Flow<Track?>

//    @Query("SELECT name, plays, listeners, imageUrl FROM tracks WHERE name = :name")
//    suspend fun getTrackMinimal(name: String): MinimalEntity
}

@Dao
interface RelationshipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceName = :name AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedAlbums(name: String, sourceType: ListingType, targetType: ListingType = ListingType.ALBUM): Flow<List<RelatedAlbum>>

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceName = :name AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedTracks(name: String, sourceType: ListingType, targetType: ListingType = ListingType.TRACK): Flow<List<RelatedTrack>>

    @Transaction
    @Query("SELECT * FROM relations WHERE sourceName = :name AND sourceType = :sourceType AND targetType = :targetType ORDER BY `index` ASC")
    fun getRelatedArtists(name: String, sourceType: ListingType, targetType: ListingType = ListingType.ARTIST): Flow<List<RelatedArtist>>
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