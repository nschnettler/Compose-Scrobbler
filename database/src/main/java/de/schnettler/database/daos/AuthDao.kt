package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.Session
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AuthDao : BaseDao<AuthToken> {
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