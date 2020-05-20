package de.schnettler.database.daos

import androidx.room.*
import de.schnettler.database.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM session LIMIT 1")
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

    @Query("DELETE FROM table_auth WHERE tokenType = :type")
    suspend fun deleteAuthToken(type: String)

    @Query("SELECT * FROM table_auth WHERE tokenType = :type")
    fun getAuthToken(type: String): Flow<AuthToken?>
}


@Dao
interface ChartDao {
    @Transaction
    @Query("SELECT * FROM table_charts WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: String): Flow<List<ListEntryWithArtist>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(entries: List<ListEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artist: List<Artist>)

    @Transaction
    suspend fun insertTopArtists(artistEntry: List<ListEntryWithArtist>) {
        insertTopList(artistEntry.map { it.listing })
        insertArtists(artistEntry.map { it.artist })
    }
}

@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM table_charts WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: String): Flow<List<ListEntryWithArtist>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(entries: List<ListEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artist: List<Artist>)

    @Transaction
    suspend fun insertTopArtists(artistEntry: List<ListEntryWithArtist>) {
        insertTopList(artistEntry.map { it.listing })
        insertArtists(artistEntry.map { it.artist })
    }
}