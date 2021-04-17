package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.User
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : BaseDao<User> {
    @Query("SELECT * FROM users LIMIT 1")
    abstract fun getUser(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    abstract fun getUserOnce(): User?

    @Query("UPDATE users SET artistCount = :count WHERE name = :userName")
    abstract suspend fun updateArtistCount(userName: String, count: Long): Int

    @Query("UPDATE users SET lovedTracksCount = :count WHERE name = :userName")
    abstract suspend fun updateLovedTracksCount(userName: String, count: Long)
}