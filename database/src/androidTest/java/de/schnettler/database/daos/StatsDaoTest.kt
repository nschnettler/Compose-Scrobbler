package de.schnettler.database.daos

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.DataGenerator.generateStats
import de.schnettler.database.models.Stats
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class StatsDaoTest : DatabaseTest() {

    @Test
    fun insertOrUpdateStats_statNotInDb_insertsStat() = runBlockingTest {
        // GIVEN - Empty db
        val stat = generateStats(1).first()

        // WHEN - Stat is inserted/updated
        db.statDao().insertOrUpdateStats(listOf(stat))

        // THEN - Stat is simply inserted
        val loaded = db.statDao().getStat(stat.id)
        expect(loaded).toBe(stat)
    }

    @Test
    fun insertOrUpdateStats_updatingWithUserPlaysMinusOne_updatesPublicStats() = runBlockingTest {
        // GIVEN - Stats in DB
        val stat = listOf(
            Stats("stat1", 10, 20, 30),
            Stats("stat2", 45, 52, 68),
            Stats("stat3", 17, 85, 78),
        )
        db.statDao().insertAll(stat)

        // WHEN - Stat with userplays = -1 is inserted/updated
        val newPlays = 230L
        val newListeners = 545L
        val updatedStat = stat[2].copy(plays = newPlays, listeners = newListeners, userPlays = -1)
        db.statDao().insertOrUpdateStats(listOf(updatedStat))

        // THEN - Stat is updated: Only public stats changed, userplays stays the same
        val loaded = db.statDao().getStat(updatedStat.id)
        expect(loaded).toBe(stat[2].copy(plays = newPlays, listeners = newListeners))
    }

    @Test
    fun insertOrUpdateStats_updatingWithUserPlaysNotZero_updatesAllStats() = runBlockingTest {
        // GIVEN - Stats in DB
        val stat = listOf(
            Stats("stat1", 10, 20, 30),
            Stats("stat2", 45, 52, 68),
            Stats("stat3", 17, 85, 78),
        )
        db.statDao().insertAll(stat)

        // WHEN - Stat with userplays = 0 is inserted/updated
        val updatedStat = stat[2].copy(plays = 203, listeners = 545, userPlays = 654)
        db.statDao().insertOrUpdateStats(listOf(updatedStat))

        // THEN - Stats is completely updated
        val loaded = db.statDao().getStat(updatedStat.id)
        expect(loaded).toBe(updatedStat)
    }

    @Test
    fun updatePublicStats_updatesPublicStatsOfOneEntity() = runBlockingTest {
        // GIVEN - Multiple Stats in DB
        val stats = generateStats(10)
        val toBeTested = stats[3]
        db.statDao().insertAll(stats)
        val updatedPlays = 57L
        val updatedListeners = 365L

        // WHEN - Updating Public Stats
        val changedRows = db.statDao().updatePublicStats(toBeTested.id, updatedPlays, updatedListeners)
        val result = db.statDao().getStat(toBeTested.id)

        // THEN - 1. Updated one Row, 2. Updated the right row 3. Updated only public stats
        expect(changedRows).toBe(1)
        expect(result).toBe(toBeTested.copy(plays = updatedPlays, listeners = updatedListeners))
    }

    @Test
    fun updateUserStats() = runBlockingTest {
        // GIVEN - Multiple Stats in DB
        val stats = generateStats(10)
        val toBeTested = stats[3]
        db.statDao().insertAll(stats)
        val updatedPlays = 57L

        // WHEN - Updating Public Stats
        val changedRows = db.statDao().updateUserStats(toBeTested.id, updatedPlays)
        val result = db.statDao().getStat(toBeTested.id)

        // THEN - 1. Updated one Row, 2. Updated the right row 3. Updated only user stats
        expect(changedRows).toBe(1)
        expect(result).toBe(toBeTested.copy(userPlays = updatedPlays))
    }
}