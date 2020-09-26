package de.schnettler.repo

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.repo.mapping.response.LastFmResponse
import java.net.SocketTimeoutException
import org.junit.Test

class ScrobbleRepositoryTest {
    @Test
    fun `when safePost returns successfully then it should emit the result as success`() {
        val expected = LastFmResponse.SUCCESS("Data")
        val result = safePost { expected }
        expect(result).toBe(expected)
    }

    @Test
    fun `when safePost throws exception then it should emit the result as exception`() {
        val result = safePost<String> { throw SocketTimeoutException() }
        expect(result).isA<LastFmResponse.EXCEPTION>().feature { f(it::exception) }.isA<SocketTimeoutException>()
    }
}