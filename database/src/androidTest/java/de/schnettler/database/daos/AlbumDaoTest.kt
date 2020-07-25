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
import de.schnettler.database.models.Album
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: AlbumDao

    @Before
    fun initDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = db.albumDao()
    }

    @After
    fun closeDatabase() = db.close()

    @Test
    fun insertOrUpdateStatsInsertsWhenNotExisting() = runBlockingTest {
        // GIVEN a database which does not include the following tracks
        val albums = generateTopAlbums(5)

        // WHEN Artist Track is inserted
        dao.insertOrUpdateStats(albums)

        // THEN Tracks are inserted normally
        albums.forEach { album ->
            dao.getAlbum(album.id, album.artist!!).collectValue { loaded ->
                expect(loaded).toBe(album)
            }
        }
    }

    @Test
    fun insertOrUpdateStatsUpdatesWhenExisting() = runBlockingTest {
        // GIVEN a database which does conmtain the following tracks
        val albums = generateTopAlbums(5)
        dao.forceInsertAll(albums)

        // WHEN Artist Track with the same ids and artists are inserted
        val newAlbums = albums.mapIndexed { index, track ->
            track.copy(url = "newUrl$index", plays = 100L + index)
        }
        dao.insertOrUpdateStats(newAlbums)

        // THEN Only Stats are updated
        albums.forEachIndexed { index, album ->
            dao.getAlbum(album.id, album.artist!!).collectValue { loaded ->
                val changedTrack = newAlbums[index]
                expect(loaded?.plays).toBe(changedTrack.plays)
                expect(loaded?.plays).notToBe(album.plays)
                expect(loaded?.url).toBe(album.url)
                expect(loaded?.url).notToBe(changedTrack.url)
            }
        }
    }
}

fun generateTopAlbums(num: Int): List<Album> {
    val result = mutableListOf<Album>()
    for (i in 0..num) {
        result.add(
            Album(
                name = "album$i",
                url = "url$i",
                plays = 10L + i,
                artist = "artist",
                imageUrl = "image$i"
            )
        )
    }
    return result
}