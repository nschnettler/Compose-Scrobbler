package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.db.DataGenerator.generateAlbumWithTopListEntry
import de.schnettler.scrobbler.db.DataGenerator.generateAlbums
import de.schnettler.scrobbler.db.DataGenerator.generateArtistWithTopListEntry
import de.schnettler.scrobbler.db.DataGenerator.generateArtists
import de.schnettler.scrobbler.db.DataGenerator.generateTracks
import de.schnettler.scrobbler.db.DataGenerator.generateTracksWithTopListEntry
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ChartDaoTest : de.schnettler.scrobbler.DatabaseTest() {

    @Test
    fun getTopArtists_userTopArtists_returnsUserTopArtists() = runBlockingTest {
        // GIVEN - Top list artists in db
        val userTop = generateArtistWithTopListEntry(10, ListType.USER)
        val chartTop = generateArtistWithTopListEntry(10, ListType.CHART)
        val other = generateArtists(10, "additionalArtist")
        val artists = (userTop.map { it.value } + chartTop.map { it.value } + other).shuffled()
        val entries = (userTop.map { it.listing } + chartTop.map { it.listing }).shuffled()
        db.artistDao().insertAll(artists)
        db.chartDao().insertAll(entries)

        // WHEN - User TopArtists are requested
        val userTopArtists = db.chartDao().getTopArtists(EntityType.ARTIST, ListType.USER)

        // THEN - Returned the user topArtists, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
        }
    }

    @Test
    fun getTopArtists_chartTopArtists_returnsChartTopArtists() = runBlockingTest {
        // GIVEN - Top list artists in db
        val userTop = generateArtistWithTopListEntry(10, ListType.USER)
        val chartTop = generateArtistWithTopListEntry(10, ListType.CHART)
        val other = generateArtists(10, "additionalArtist")
        val artists = (userTop.map { it.value } + chartTop.map { it.value } + other).shuffled()
        val entries = (userTop.map { it.listing } + chartTop.map { it.listing }).shuffled()
        db.artistDao().insertAll(artists)
        db.chartDao().insertAll(entries)

        // WHEN - User TopArtists are requested
        val userTopArtists = db.chartDao().getTopArtists(EntityType.ARTIST, ListType.CHART)

        // THEN - Returned the user topArtists, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(chartTop)
        }
    }

    @Test
    fun getTopArtists_allArtistsMissing_throwsException() = runBlockingTest {
        // GIVEN - Toplist entries in db. All Artists missing.
        val userTop = generateArtistWithTopListEntry(10, ListType.USER)
        db.chartDao().insertAll(userTop.map { it.listing })

        // WHEN - ArtistTopList is requested
        val userTopArtists = db.chartDao().getTopArtists(EntityType.ARTIST, ListType.USER)

        // THEN - Throws NPE
        userTopArtists.catch { e ->
            expect(e).isA<NullPointerException>()
        }
    }

    @Test
    fun getTopArtists_subsetOfArtistsMissing_throwsException() = runBlockingTest {
        // GIVEN - Toplist entries in db. Subset of Artists missing.
        val userTop = generateArtistWithTopListEntry(10, ListType.USER)
        db.chartDao().insertAll(userTop.map { it.listing })
        db.artistDao().insertAll(userTop.map { it.value }.subList(0, userTop.size - 2))

        // WHEN - ArtistTopList is requested
        val userTopArtists = db.chartDao().getTopArtists(EntityType.ARTIST, ListType.USER)

        // THEN - Throws NPE
        userTopArtists.catch { e ->
            expect(e).isA<NullPointerException>()
        }
    }

    @Test
    fun getTopTracks_requestedUserTopTracks_returnsUserTopTracks() = runBlockingTest {
        // GIVEN - A bunch of tracks, some are part of user and chart toplists
        val userTop = generateTracksWithTopListEntry(10, ListType.USER)
        val chartTop = generateTracksWithTopListEntry(10, ListType.CHART)
        val other = generateTracks(10, "additionalTrack")
        val tracks = (userTop.map { it.value } + chartTop.map { it.value } + other).shuffled()
        val entries = (userTop.map { it.listing } + chartTop.map { it.listing }).shuffled()
        db.trackDao().insertAll(tracks)
        db.chartDao().insertAll(entries)

        // WHEN - Requested the user toptracks
        val userTopArtists = db.chartDao().getTopTracks()

        // THEN - Returned the user toptracks, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
        }
    }

    @Test
    fun getTopTracks_requestedChartTracks_returnsChartTracks() = runBlockingTest {
        // GIVEN - A bunch of tracks, some are part of user and chart toplists
        val userTop = generateTracksWithTopListEntry(10, ListType.USER)
        val chartTop = generateTracksWithTopListEntry(10, ListType.CHART)
        val other = generateTracks(10, "additionalTrack")
        db.trackDao().insertAll((userTop.map { it.value } + chartTop.map { it.value } + other).shuffled())
        db.chartDao().insertAll((userTop.map { it.listing } + chartTop.map { it.listing }).shuffled())

        // WHEN - Requested the chart toptracks
        val userTopArtists = db.chartDao().getTopTracks(listType = ListType.CHART)

        // THEN - Returned the chart toptracks, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(chartTop)
        }
    }

    @Test
    fun getTopAlbums_requestedTopAlbums_returnsTopAlbums() = runBlockingTest {
        // GIVEN - A bunch of tracks, some are part of user and chart toplists
        val userTop = generateAlbumWithTopListEntry(10, ListType.USER)
        val chartTop = generateAlbumWithTopListEntry(10, ListType.CHART)
        val other = generateAlbums(10, "additionalAlbum")
        db.albumDao().insertAll((userTop.map { it.value } + chartTop.map { it.value } + other).shuffled())
        db.chartDao().insertAll((userTop.map { it.listing } + chartTop.map { it.listing }).shuffled())

        // WHEN - Requested the user toptracks
        val result = db.chartDao().getTopAlbums()

        // THEN - Returned the user toptracks, ordered by index ASC
        result.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
        }
    }

    @Test
    fun getArtistsWithoutImages_returnsOnlyArtistsWithoutImages() = runBlockingTest {
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
        val result = db.chartDao().getArtistsWithoutImages()

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
        val result = db.chartDao().getTracksWithoutImages()

        // THEN - Return only tracks which are a) in User Toplist b) have no image yet
        expect(result.isNotEmpty()).toBe(true)
        expect(result).containsExactlyElementsOf(tracks.filter { it.imageUrl == null })
    }
}