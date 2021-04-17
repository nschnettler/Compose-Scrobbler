package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.Stats
import de.schnettler.scrobbler.persistence.BaseDao

@Dao
abstract class StatsDao : BaseDao<Stats> {
    @Query("SELECT * FROM stats WHERE id = :id")
    abstract suspend fun getStat(id: String): Stats?

    suspend fun insertOrUpdateStats(stats: List<Stats?>) {
        val result = insertAll(stats)
        result.forEachIndexed { index, value ->
            if (value == -1L) {
                stats[index]?.let { stat ->
                    updatePublicStats(stat.id, stat.plays, stat.listeners)
                    if (stat.userPlays >= 0) updateUserStats(stat.id, stat.userPlays)
                }
            }
        }
    }

    @Query("UPDATE stats SET plays = :plays, listeners = :listeners WHERE id = :id")
    abstract fun updatePublicStats(id: String, plays: Long, listeners: Long): Int

    @Query("UPDATE stats SET userPlays = :userPlays WHERE id = :id")
    abstract fun updateUserStats(id: String, userPlays: Long): Int
}