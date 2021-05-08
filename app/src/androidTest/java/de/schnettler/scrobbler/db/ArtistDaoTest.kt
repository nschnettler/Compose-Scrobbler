package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.util.DataGenerator.generateArtists
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ArtistDaoTest : DatabaseTest() {

    @Test
    fun getArtist_artistPresent_returnsArtist() = runBlockingTest {
        // GIVE - multiple Artists in db
        val artistNumber = 10
        val artists = generateArtists(artistNumber)
        db.artistDao().insertAll(artists)

        // WHEN - One Artist is requested
        val artistToRequest = artists[7]
        val result = db.artistDao().getArtist(artistToRequest.id)

        // THEN - The requested artist should be returned
        result.collectValue {
            expect(it).toBe(artistToRequest)
        }
    }

    @Test
    fun getArtist_artistNotPresent_returnsNull() = runBlockingTest {
        // GIVE - multiple Artists in db
        val artistNumber = 10
        val artists = generateArtists(artistNumber)
        db.artistDao().insertAll(artists)

        // WHEN - One Artist is requested
        val result = db.artistDao().getArtist("UnknownArtistId")

        // THEN - The requested artist should be returned
        result.collectValue {
            expect(it).toBe(null)
        }
    }

//    @Test
//    fun getArtistImageUrl_imageUrlPresent_returnsImageUrl() = runBlockingTest {
//        // GIVEN - Artist in Database
//        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "ImageUrl")
//        db.artistDao().insert(artist)
//
//        // WHEN - Get artist image Url
//        val imageUrl = db.artistDao().getArtistImageUrl(id = artist.id)
//
//        // THEN - Loaded ImageUrl (only)
//        expect(imageUrl).toBe(artist.imageUrl)
//    }
//
//    @Test
//    fun updateArtistImageUrl_imageUrlNotPresent_insertsImageUrl() = runBlockingTest {
//        // GIVEN - Artist in Database
//        val artist = Artist(name = "TestArtist", url = "ArtistUrl")
//        db.artistDao().insert(artist)
//
//        // WHEN - ImageUrl inserted
//        val imageURl = "ImageUrl"
//        val changedRows = db.artistDao().updateArtistImageUrl(url = imageURl, id = artist.id)
//        val loadedImageUrl = db.artistDao().getArtistImageUrl(id = artist.id)
//
//        // THEN - ImageUrl was saved in db
//        expect(changedRows).toBe(1)
//        expect(loadedImageUrl).toBe(imageURl)
//    }
//
//    @Test
//    fun updateArtistImageUrl_imageUrlPresent_overwritesImageUrl() = runBlockingTest {
//        // GIVEN - Artist in Database
//        val artist = Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl")
//        db.artistDao().insert(artist)
//
//        // WHEN - ImageUrl inserted
//        val imageURl = "NewImageUrl"
//        val changedRows = db.artistDao().updateArtistImageUrl(url = imageURl, id = artist.id)
//        val loadedImageUrl = db.artistDao().getArtistImageUrl(id = artist.id)
//
//        // THEN - ImageUrl was saved in db
//        expect(changedRows).toBe(1)
//        expect(loadedImageUrl).toBe(imageURl)
//    }
//
//    @Test
//    fun updateArtistImageUrl_artistNotPresent_returnsZeroRowsChanged() = runBlockingTest {
//        // GIVEN - Other artist in db
//        db.artistDao()
//            .insert(Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl"))
//
//        // WHEN - ImageUrl of wrong artist updated
//        val imageURl = "ImageUrl"
//        val changedRows = db.artistDao().updateArtistImageUrl(url = imageURl, id = "WrongArtist")
//
//        // THEN - No rows changed
//        expect(changedRows).toBe(0)
//    }

//    @Test
//    fun getArtistWithMetadata_artistInfoStatsInDb_returnsArtistWithStatsAndInfo() = runBlockingTest {
//        // GIVEN - Artist, Stats and Info in DB
//        val artist = generateArtistWithStatsAndInfo(1).first()
//        db.artistDao().insert(artist.entity)
//        db.statDao().insert(artist.stats)
//        db.infoDao().insert(artist.info)
//
//        // WHEN - Requesting the artist details
//        val returned = db.artistDao().getArtistWithMetadata(artist.entity.id)
//
//        // THEN - Returned album should match inserted album
//        returned.collectValue {
//            expect(it).toBe(artist)
//        }
//    }

//    @Test
//    fun getArtistWithMetadata_onlyArtistInDb_returnsAlbumWithStatsAndInfo() = runBlockingTest {
//        // GIVEN - Only artist in DB, info and stats are missing
//        val artist = generateArtistWithStatsAndInfo(1).first()
//        db.artistDao().insert(artist.entity)
//
//        // WHEN - Requesting artist details
//        val returned = db.artistDao().getArtistWithMetadata(artist.entity.id)
//
//        // THEN - Should return ArtistWithStatsAndInfo (where stats and info are null)
//        returned.collectValue {
//            expect(it).notToBeNull()
//            expect(it?.entity).toBe(artist.entity)
//            expect(it?.stats).toBe(null)
//            expect(it?.info).toBe(null)
//        }
//    }
}