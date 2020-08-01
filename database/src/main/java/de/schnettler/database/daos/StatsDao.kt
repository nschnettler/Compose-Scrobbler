package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.Stats

@Dao
abstract class StatsDao : BaseDao<Stats> {
    suspend fun insertOrUpdateStats(stats: List<Stats>) {
        val result = insertAll(stats)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                val stat = stats[index]
                updatePublicStats(stat.id, stat.plays, stat.listeners)
                if (stat.userPlays > 0) updateUserStats(stat.id, stat.userPlays)
            }
        }
    }

    @Query("UPDATE stats SET plays = :plays, listeners = :listeners WHERE id = :id")
    abstract fun updatePublicStats(id: String, plays: Long, listeners: Long)

    @Query("UPDATE stats SET userPlays = :userPlays WHERE id = :id")
    abstract fun updateUserStats(id: String, userPlays: Long)
}