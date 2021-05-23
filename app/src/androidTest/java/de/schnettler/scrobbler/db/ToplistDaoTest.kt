package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.profile.db.ToplistDao
import de.schnettler.scrobbler.util.DataGenerator.generateAlbumWithTopListEntry
import de.schnettler.scrobbler.util.DataGenerator.generateAlbums
import de.schnettler.scrobbler.util.DataGenerator.generateArtistWithTopListEntry
import de.schnettler.scrobbler.util.DataGenerator.generateArtists
import de.schnettler.scrobbler.util.DataGenerator.generateTracks
import de.schnettler.scrobbler.util.DataGenerator.generateTracksWithTopListEntry
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

class ToplistDaoTest : DatabaseTest() {

    private lateinit var daoUnderTest: ToplistDao

    @Before
    fun setup() {
        daoUnderTest = db.toplistDao()
    }

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
        val userTopArtists = daoUnderTest.getTopArtists()

        // THEN - Returned the user topArtists, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
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
        val userTopArtists = daoUnderTest.getTopTracks()

        // THEN - Returned the user toptracks, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
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
        val result = daoUnderTest.getTopAlbums()

        // THEN - Returned the user toptracks, ordered by index ASC
        result.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(userTop)
        }
    }
}