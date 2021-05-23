package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.util.DataGenerator.generateArtistWithTopListEntry
import de.schnettler.scrobbler.util.DataGenerator.generateArtists
import de.schnettler.scrobbler.util.DataGenerator.generateTracks
import de.schnettler.scrobbler.util.DataGenerator.generateTracksWithTopListEntry
import de.schnettler.scrobbler.util.DatabaseTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ImageDaoTest : DatabaseTest() {

    @Test
    fun updateArtistImageUrl_imageUrlNotPresent_insertsImageUrl() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = LastFmEntity.Artist(name = "TestArtist", url = "ArtistUrl")
        db.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "ImageUrl"
        val changedRows = db.imageDao().updateArtistImageUrl(url = imageURl, id = artist.id)
        val loadedImageUrl = db.imageDao().getArtistImageUrl(id = artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrl_imageUrlPresent_overwritesImageUrl() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = LastFmEntity.Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl")
        db.artistDao().insert(artist)

        // WHEN - ImageUrl inserted
        val imageURl = "NewImageUrl"
        val changedRows = db.imageDao().updateArtistImageUrl(url = imageURl, id = artist.id)
        val loadedImageUrl = db.imageDao().getArtistImageUrl(id = artist.id)

        // THEN - ImageUrl was saved in db
        expect(changedRows).toBe(1)
        expect(loadedImageUrl).toBe(imageURl)
    }

    @Test
    fun updateArtistImageUrl_artistNotPresent_returnsZeroRowsChanged() = runBlockingTest {
        // GIVEN - Other artist in db
        db.artistDao()
            .insert(LastFmEntity.Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "OldImageUrl"))

        // WHEN - ImageUrl of wrong artist updated
        val imageURl = "ImageUrl"
        val changedRows = db.imageDao().updateArtistImageUrl(url = imageURl, id = "WrongArtist")

        // THEN - No rows changed
        expect(changedRows).toBe(0)
    }

    @Test
    fun getArtistImageUrl_imageUrlPresent_returnsImageUrl() = runBlockingTest {
        // GIVEN - Artist in Database
        val artist = LastFmEntity.Artist(name = "TestArtist", url = "ArtistUrl", imageUrl = "ImageUrl")
        db.artistDao().insert(artist)

        // WHEN - Get artist image Url
        val imageUrl = db.imageDao().getArtistImageUrl(id = artist.id)

        // THEN - Loaded ImageUrl (only)
        expect(imageUrl).toBe(artist.imageUrl)
    }

    @Test
    fun getTopArtistsWithoutImages_returnsOnlyArtistsWithoutImages() = runBlockingTest {
        // GIVEN - A bunch of artists, some with and some without images. Only some are part of user toplist.
        val topArtists = generateArtistWithTopListEntry(20, ListType.USER)
        val artists = topArtists.mapIndexed { index, (_, artist) ->
            if (index < (topArtists.size / 2)) artist.copy(imageUrl = "imageUrl")
            else artist
        }
        val moreArtists = generateArtists(10, "moreArtists")
        db.artistDao().insertAll((artists + moreArtists).shuffled())
        db.chartDao().insertAll(topArtists.map { it.listing })

        // WHEN - Artists without images are requested
        val result = db.imageDao().getTopArtistsWithoutImages()

        // THEN - Return only artists which are a) in User Toplist b) have no image yet
        expect(result.isNotEmpty()).toBe(true)
        expect(result).containsExactlyElementsOf(artists.filter { it.imageUrl == null })
    }

    @Test
    fun getTracksWithoutImages_returnsOnlyTracksWithoutImages() = runBlockingTest {
        // GIVEN - A bunch of tracks, some with and some without images. Only some are part of user toplist.
        val top = generateTracksWithTopListEntry(20, ListType.USER)
        val tracks = top.mapIndexed { index, (_, track) ->
            if (index < (top.size / 2)) track.copy(imageUrl = "imageUrl")
            else track
        }
        val more = generateTracks(10, "moreArtists")
        db.trackDao().insertAll((tracks + more).shuffled())
        db.chartDao().insertAll(top.map { it.listing })

        // WHEN - Tracks without images are requested
        val result = db.imageDao().getTopTracksWithoutImages()

        // THEN - Return only tracks which are a) in User Toplist b) have no image yet
        expect(result.isNotEmpty()).toBe(true)
        expect(result).containsExactlyElementsOf(tracks.filter { it.imageUrl == null })
    }
}