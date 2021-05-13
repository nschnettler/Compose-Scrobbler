package de.schnettler.scrobbler.profile.db

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.User
import de.schnettler.scrobbler.persistence.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : BaseDao<User> {
    @Query("SELECT * FROM users LIMIT 1")
    abstract fun getUser(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    abstract fun getUserOnce(): User?

    @Query("UPDATE users SET artistCount = :count")
    abstract suspend fun updateArtistCount(count: Long): Int

    @Query("UPDATE users SET lovedTracksCount = :count WHERE name = :userName")
    abstract suspend fun updateLovedTracksCount(userName: String, count: Long)
}