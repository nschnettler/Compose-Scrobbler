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
}