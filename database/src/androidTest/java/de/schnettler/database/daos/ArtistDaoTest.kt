package de.schnettler.database.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.AppDatabase
import de.schnettler.database.collectValue
import de.schnettler.database.models.Artist
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ArtistDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun getArtistRetrievesData() = runBlockingTest {
        //GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        database.artistDao().insert(artist)

        //WHEN - Get the artist by id from db
        val loadedArtist = database.artistDao().getArtist(artist.id)

        //THEN - Loaded Data is equal to artist
        loadedArtist.collectValue {
            expect(it).notToBeNull().toBe(artist)
        }
    }

    @Test
    fun getArtistImageUrlReturnsImageURlOnly() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "ImageUrl")
        database.artistDao().insert(artist)

        // WHEN - Get artist image Url
        val imageUrl = database.artistDao().getArtistImageUrl(artist.id)

        // THEN - Loaded ImageUrl (only)
        expect(imageUrl).toBe(artist.imageUrl)
    }

    @Test
    fun updateArtistImageUrlWhenFirstValue() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        database.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "ImageUrl"
        val changedRows = database.artistDao().updateArtistImageUrl(imageURl, artist.id)
        val loadedImageUrl = database.artistDao().getArtistImageUrl(artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrlOverwritesOldValue() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl")
        database.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "NewImageUrl"
        val changedRows = database.artistDao().updateArtistImageUrl(imageURl, artist.id)
        val loadedImageUrl = database.artistDao().getArtistImageUrl(artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrlNoMatch() = runBlockingTest {
        // GIVEN - Other artist in db
        database.artistDao().insert(Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl"))

        // WHEN - ImageUrl of wrong artist updated
        val imageURl = "ImageUrl"
        val changedRows = database.artistDao().updateArtistImageUrl(imageURl, "WrongArtist")

        // THEN - No rows changed
        expect(changedRows).toBe(0)
    }
}