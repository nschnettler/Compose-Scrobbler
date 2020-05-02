package de.schnettler.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.schnettler.database.models.Session

@Dao
interface AuthDao {
    @Query("SELECT * FROM session LIMIT 1")
    fun getSession(): LiveData<Session?>

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)
}