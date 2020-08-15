package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Session
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SessionDao : BaseDao<Session> {
    @Query("SELECT * FROM sessions LIMIT 1")
    abstract fun getSession(): Flow<Session?>
}