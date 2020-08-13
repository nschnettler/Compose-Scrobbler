package de.schnettler.database.daos

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.collectValue
import de.schnettler.database.models.LastFmEntity.Artist
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith

class ArtistDaoTest : DatabaseTest() {

    @Test
    fun getArtistRetrievesData() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - Get the artist by id from db
        val loadedArtist = db.artistDao().getArtist(artist.id)

        // THEN - Loaded Data is equal to artist
        loadedArtist.collectValue {
            expect(it).notToBeNull().toBe(artist)
        }
    }

    @Test
    fun getArtistImageUrlReturnsImageURlOnly() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "ImageUrl")
        db.artistDao().insert(artist)

        // WHEN - Get artist image Url
        val imageUrl = db.artistDao().getArtistImageUrl(artist.id)

        // THEN - Loaded ImageUrl (only)
        expect(imageUrl).toBe(artist.imageUrl)
    }

    @Test
    fun updateArtistImageUrlWhenFirstValue() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "ImageUrl"
        val changedRows = db.artistDao().updateArtistImageUrl(imageURl, artist.id)
        val loadedImageUrl = db.artistDao().getArtistImageUrl(artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrlOverwritesOldValue() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl")
        db.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "NewImageUrl"
        val changedRows = db.artistDao().updateArtistImageUrl(imageURl, artist.id)
        val loadedImageUrl = db.artistDao().getArtistImageUrl(artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrlNoMatch() = runBlockingTest {
        // GIVEN - Other artist in db
        db.artistDao()
            .insert(Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl"))

        // WHEN - ImageUrl of wrong artist updated
        val imageURl = "ImageUrl"
        val changedRows = db.artistDao().updateArtistImageUrl(imageURl, "WrongArtist")

        // THEN - No rows changed
        expect(changedRows).toBe(0)
    }
}