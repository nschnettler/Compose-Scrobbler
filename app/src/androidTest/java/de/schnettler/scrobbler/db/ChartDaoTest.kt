package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.util.DataGenerator.generateArtistWithTopListEntry
import de.schnettler.scrobbler.util.DataGenerator.generateArtists
import de.schnettler.scrobbler.util.DataGenerator.generateTracks
import de.schnettler.scrobbler.util.DataGenerator.generateTracksWithTopListEntry
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ChartDaoTest : DatabaseTest() {

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
        val userTopArtists = db.chartDao().getTopArtists()

        // THEN - Returned the user topArtists, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(chartTop)
        }
    }

    @Test
    fun getTopArtists_allArtistsMissing_throwsException() = runBlockingTest {
        // GIVEN - Toplist entries in db. All Artists missing.
        val userTop = generateArtistWithTopListEntry(10, ListType.CHART)
        db.chartDao().insertAll(userTop.map { it.listing })

        // WHEN - ArtistTopList is requested
        val userTopArtists = db.chartDao().getTopArtists()

        // THEN - Throws NPE
        userTopArtists.catch { e ->
            expect(e).isA<NullPointerException>()
        }
    }

    @Test
    fun getTopArtists_subsetOfArtistsMissing_throwsException() = runBlockingTest {
        // GIVEN - Toplist entries in db. Subset of Artists missing.
        val userTop = generateArtistWithTopListEntry(10, ListType.CHART)
        db.chartDao().insertAll(userTop.map { it.listing })
        db.artistDao().insertAll(userTop.map { it.value }.subList(0, userTop.size - 2))

        // WHEN - ArtistTopList is requested
        val userTopArtists = db.chartDao().getTopArtists()

        // THEN - Throws NPE
        userTopArtists.catch { e ->
            expect(e).isA<NullPointerException>()
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
        val userTopArtists = db.chartDao().getTopTracks()

        // THEN - Returned the chart toptracks, ordered by index ASC
        userTopArtists.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(chartTop)
        }
    }
}