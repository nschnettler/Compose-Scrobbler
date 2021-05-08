package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.isGreaterThanOrEqual
import ch.tutteli.atrium.api.fluent.en_GB.notToBe
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class BaseDaoTest : DatabaseTest() {

    @Test
    fun forceInsertOverwritesData() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - Artist data changes and is updated in db
        val newArtist = artist.copy(url = "NewUrl")
        db.artistDao().forceInsert(newArtist)
        val loadedArtist = db.artistDao().getArtist(newArtist.id)

        // THEN - newArtist overwrites artist in database
        loadedArtist.collectValue {
            expect(it).notToBeNull().toBe(newArtist)
            expect(it).notToBe(artist)
        }
    }

    @Test
    fun insertDoesNotOverwriteData() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - Artist data changes and is updated in db
        val newArtist = artist.copy(url = "NewUrl")
        db.artistDao().insert(newArtist)
        val loadedArtist = db.artistDao().getArtist(newArtist.id)

        // THEN - artist is not overwritten by newArtist
        loadedArtist.collectValue {
            expect(it).notToBe(newArtist)
            expect(it).toBe(artist)
        }
    }

    @Test
    fun deleteRemovesData() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - Deleted Artist
        db.artistDao().delete(artist)
        val loadedArtist = db.artistDao().getArtist(artist.id)

        // THEN - Artist was removed from db
        loadedArtist.collectValue {
            expect(it).toBe(null)
        }
    }

    @Test
    fun insertAllInsertsDataWithoutOverwrite() = runBlockingTest {
        // GIVEN - Database with one Artist
        val artist = Artist(name = "Artist1", url = "Url1")
        db.artistDao().insert(artist)

        // WHEN - A list of Artists is inserted and one has the same id as the database artist
        val artists = listOf(
            Artist(name = "Artist1", url = "Url1"),
            Artist(name = "Artist2", url = "Url2"),
            Artist(name = "Artist3", url = "Url3")
        )
        val changedRows = db.artistDao().insertAll(artists)

        // THAN - Only two Artists are inserted. One is ignored.
        expect(changedRows.size).toBe(artists.size)
        changedRows.forEachIndexed { index, item ->
            when (index) {
                0 -> expect(item).toBe(-1L)
                else -> expect(item).isGreaterThanOrEqual(0L)
            }
        }
    }

    fun forceInsertAllInsertsDataWithOverwrite() = runBlockingTest {
        // GIVEN - Database with one Artist
        val artist = Artist(name = "Artist1", url = "Url1")
        db.artistDao().insert(artist)

        // WHEN - A list of Artists is inserted and one has the same id as the database artist
        val artists = listOf(
            Artist(name = "Artist1", url = "Url1"),
            Artist(name = "Artist2", url = "Url2"),
            Artist(name = "Artist3", url = "Url3")
        )
        val changedRows = db.artistDao().forceInsertAll(artists)

        // THAN - Only two Artists are inserted. One is ignored.
        expect(changedRows.size).toBe(artists.size)
        changedRows.forEach { item ->
            expect(item).isGreaterThanOrEqual(0L)
        }
    }
}