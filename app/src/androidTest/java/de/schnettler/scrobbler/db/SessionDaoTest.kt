package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.authentication.model.Session
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class SessionDaoTest : de.schnettler.scrobbler.DatabaseTest() {

    @Test
    fun getSession_sessionAvailable_returnsOneSession() = runBlockingTest {
        // GIVEN - Multiple Sessions in db
        val sessions = listOf(
            de.schnettler.scrobbler.authentication.model.Session("session1", "key1", 0),
            de.schnettler.scrobbler.authentication.model.Session("session2", "key2", 0)
        )
        db.sessionDao().insertAll(sessions)

        // WHEN - Session is requested
        val returned = db.sessionDao().getSession()

        // THEN - Exactly one Session is returned
        returned.collectValue {
            expect(it).notToBeNull()
            expect(it).isA<de.schnettler.scrobbler.authentication.model.Session>()
            expect(it).toBe(sessions.first())
        }
    }

    @Test
    fun getSession_sessionUnavailable_returnsNull() = runBlockingTest {
        // GIVEN - No Sessions in db

        // WHEN - Session is requested
        val returned = db.sessionDao().getSession()

        // THEN - No Session is returned
        returned.collectValue {
            expect(it).toBe(null)
        }
    }
}