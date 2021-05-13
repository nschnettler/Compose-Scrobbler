package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.util.DataGenerator
import de.schnettler.scrobbler.util.DatabaseTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class TrackDaoTest : DatabaseTest() {

    @Test
    fun getTrack_parameterValid_returnsTrack() = runBlockingTest {
        // GIVEN - Tracks in Database
        val tracks = listOf(
            LastFmEntity.Track("Track1", "url1", "artist1"),
            LastFmEntity.Track("Track2", "url2", "artist2")
        )
        db.trackDao().insertAll(tracks)

        // WHEN - Get track by valid name
        val result = db.trackDao().getTrack(tracks.first().id, tracks.first().artist)

        // THEN - Loaded the right track
        expect(result).toBe(tracks.first())
    }

    @Test
    fun getTrack_parameterInvalid_returnsNull() = runBlockingTest {
        // GIVEN - Tracks in Database
        val tracks = DataGenerator.generateTracks(2)
        db.trackDao().insertAll(tracks)

        // WHEN - wrong artist name used
        val result = db.trackDao().getTrack(tracks.first().name, tracks.last().artist)

        // THEN - Returns null
        expect(result).toBe(null)
    }
}