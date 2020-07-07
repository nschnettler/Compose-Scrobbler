package de.schnettler.database.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.tutteli.atrium.api.fluent.en_GB.notToBe
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.AppDatabase
import de.schnettler.database.collectValue
import de.schnettler.database.models.Track
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: TrackDao

    @Before
    fun initDatabase() {
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
        ).build()
        dao = db.trackDao()
    }

    @After
    fun closeDatabase() = db.close()

    @Test
    fun insertOrUpdateStatsInsertsWhenNotExisting() = runBlockingTest {
        // GIVEN a database which does not include the following tracks
        val tracks = generateTracks(5)

        // WHEN Artist Track is inserted
        dao.insertOrUpdateStats(tracks)

        // THEN Tracks are inserted normally
        tracks.forEach {track ->
            dao.getTrack(track.id, track.artist).collectValue {loaded ->
                expect(loaded?.track).toBe(track)
            }

        }
    }

    @Test
    fun insertOrUpdateStatsUpdatesWhenExisting() = runBlockingTest {
        // GIVEN a database which does conmtain the following tracks
        val tracks = generateTracks(5)
        dao.forceInsertAll(tracks)

        // WHEN Artist Track with the same ids and artists are inserted
        val newTracks = tracks.mapIndexed { index, track ->
            track.copy(url = "newUrl$index", plays = 100L+index, listeners = 200L+index)
        }
        dao.insertOrUpdateStats(newTracks)

        // THEN Only Stats are updated
        tracks.forEachIndexed { index, track ->
            dao.getTrack(track.id, track.artist).collectValue { loaded ->
                val loadedTrack = loaded?.track
                val changedTrack = newTracks[index]
                expect(loadedTrack?.plays).toBe(changedTrack.plays)
                expect(loadedTrack?.plays).notToBe(track.plays)
                expect(loadedTrack?.listeners).toBe(changedTrack.listeners)
                expect(loadedTrack?.listeners).notToBe(track.listeners)
                expect(loadedTrack?.url).toBe(track.url)
                expect(loadedTrack?.url).notToBe(changedTrack.url)
            }
        }
    }
}

fun generateTracks(num: Int): List<Track> {
    val result = mutableListOf<Track>()
    for (i in 0..num) {
        result.add(Track(name = "Track$i", plays = 10L+i, url = "url$i", artist = "artist", listeners = 100))
    }
    return result
}