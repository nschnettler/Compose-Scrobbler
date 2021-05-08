package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Session
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SessionDao : BaseDao<Session> {
    @Query("SELECT * FROM sessions LIMIT 1")
    abstract fun getSession(): Flow<Session?>

    @Query("SELECT * FROM sessions LIMIT 1")
    abstract suspend fun getSessionOnce(): Session?
}