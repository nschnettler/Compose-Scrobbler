package de.schnettler.scrobbler.authentication.db

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.persistence.dao.BaseDao
import de.schnettler.scrobbler.authentication.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SessionDao : BaseDao<Session> {
    @Query("SELECT * FROM sessions LIMIT 1")
    abstract fun getSession(): Flow<Session?>

    @Query("SELECT * FROM sessions LIMIT 1")
    abstract suspend fun getSessionOnce(): Session?
}