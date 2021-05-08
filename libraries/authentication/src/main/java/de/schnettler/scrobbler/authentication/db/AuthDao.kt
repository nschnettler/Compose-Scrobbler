package de.schnettler.scrobbler.authentication.db

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.persistence.dao.BaseDao
import de.schnettler.scrobbler.authentication.model.AuthToken
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AuthDao : BaseDao<AuthToken> {
    @Query("SELECT * FROM auth WHERE tokenType = :type")
    abstract fun getAuthToken(type: String): Flow<AuthToken?>
}