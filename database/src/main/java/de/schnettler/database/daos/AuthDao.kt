package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.AuthToken
import de.schnettler.scrobbler.persistence.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AuthDao : BaseDao<AuthToken> {
    @Query("SELECT * FROM auth WHERE tokenType = :type")
    abstract fun getAuthToken(type: String): Flow<AuthToken?>
}