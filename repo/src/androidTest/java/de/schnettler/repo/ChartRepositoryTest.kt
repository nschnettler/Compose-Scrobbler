package de.schnettler.repo

import ch.tutteli.atrium.api.verbs.expect
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ChartRepositoryTest : DatabaseTest() {
    private lateinit var repo: de.schnettler.scrobbler.charts.repo.ChartRepository
    private lateinit var service: TestService
    @Before
    fun setupRepo() {
        service = TestService()
        repo =
            de.schnettler.scrobbler.charts.repo.ChartRepository(db.chartDao(), db.artistDao(), db.trackDao(), service)
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