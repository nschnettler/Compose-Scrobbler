package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.authentication.model.AuthToken
import de.schnettler.scrobbler.authentication.model.AuthTokenType
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class AuthDaoTest : de.schnettler.scrobbler.DatabaseTest() {

    @Test
    fun getAuthToken_tokenAvailable_returnsOneSession() = runBlockingTest {
        // GIVEN - Multiple Token - incl. Spotify - in db
        val type = de.schnettler.scrobbler.authentication.model.AuthTokenType.Spotify.value
        val tokens = listOf(
            de.schnettler.scrobbler.authentication.model.AuthToken("unknownType", "token", "type", 0),
            de.schnettler.scrobbler.authentication.model.AuthToken(type, "token", "type", 0),
            de.schnettler.scrobbler.authentication.model.AuthToken("otherType", "otherToken", "type", 0)
        )
        db.authDao().insertAll(tokens)

        // WHEN - Spotify Token is requested
        val returned = db.authDao().getAuthToken(type)

        // THEN - The right Token is returned
        returned.collectValue {
            expect(it).toBe(tokens[1])
        }
    }

    @Test
    fun getAuthToken_tokenUnavailable_returnsNull() = runBlockingTest {
        // GIVEN - No spotify Token in db
        val tokens = listOf(
            de.schnettler.scrobbler.authentication.model.AuthToken("unknownType", "token", "type", 0),
            de.schnettler.scrobbler.authentication.model.AuthToken("otherType", "otherToken", "type", 0)
        )

        // WHEN - Spotify Token is requested
        val type = de.schnettler.scrobbler.authentication.model.AuthTokenType.Spotify.value
        val returned = db.authDao().getAuthToken(type)

        // THEN - No Token is returned
        returned.collectValue {
            expect(it).toBe(null)
        }
    }
}