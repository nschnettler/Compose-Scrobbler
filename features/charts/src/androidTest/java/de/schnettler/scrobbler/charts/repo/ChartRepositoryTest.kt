package de.schnettler.scrobbler.charts.repo

import app.cash.turbine.test
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.charts.api.TestApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

class ChartRepositoryTest : DatabaseTest() {
    private lateinit var repo: ChartRepository
    private lateinit var service: TestApi
    @Before
    fun setupRepo() {
        service = TestApi()
        repo = ChartRepository(db.chartDao(), db.artistDao(), db.trackDao(), service)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun chartArtistsStoreStream_emptySOT_loadsAndInsertsData() = runBlocking {
        // GIVEN - No data in db

        // WHEN - artist charts are streamed for the first time
        val result = repo.chartArtistsStore.stream(StoreRequest.cached("", true))

        // THEN - States: no data -> loading -> data, Data: Size is equal to size returned from service
        val expectedSize = service.getTopArtists().size
        result.test() {
            val first = expectItem()
            expect(first).isA<StoreResponse.Data<Any>>()
            expect(first.dataOrNull()).toBe(emptyList())

            expect(expectItem()).isA<StoreResponse.Loading>()

            val third = expectItem()
            expect(third).isA<StoreResponse.Data<Any>>()
            expect(third.dataOrNull()).notToBeNull().hasSize(expectedSize)
        }
    }
}