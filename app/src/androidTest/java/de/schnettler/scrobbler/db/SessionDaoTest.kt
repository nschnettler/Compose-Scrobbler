package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.authentication.model.Session
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class SessionDaoTest : DatabaseTest() {

    @Test
    fun getSession_sessionAvailable_returnsOneSession() = runBlockingTest {
        // GIVEN - Multiple Sessions in db
        val sessions = listOf(
            Session("session1", "key1", 0),
            Session("session2", "key2", 0)
        )
        db.sessionDao().insertAll(sessions)

        // WHEN - Session is requested
        val returned = db.sessionDao().getSession()

        // THEN - Exactly one Session is returned
        returned.collectValue {
            expect(it).notToBeNull()
            expect(it).isA<Session>()
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