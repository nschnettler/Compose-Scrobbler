package de.schnettler.scrobbler.charts.repo

import app.cash.turbine.test
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.charts.repo.api.ChartApiFake
import de.schnettler.scrobbler.charts.repo.db.ArtistDaoFake
import de.schnettler.scrobbler.charts.repo.db.ChartDaoFake
import de.schnettler.scrobbler.charts.repo.db.TrackDaoFake
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.ExperimentalTime

class ChartRepositoryTest {
    val chartApiFake = ChartApiFake()

    private val repositoryUnderTest = ChartRepository(
        ChartDaoFake(),
        ArtistDaoFake(),
        TrackDaoFake(),
        chartApiFake
    )

    @OptIn(ExperimentalTime::class)
    @Test
    fun chartArtistsStoreStream_emptySOT_loadsAndInsertsData() = runBlocking {
        // GIVEN - No data in db

        // WHEN - artist charts are streamed for the first time
        val result = repositoryUnderTest.chartArtistsStore.stream(StoreRequest.cached("", true))

        // THEN - States: no data -> loading -> data, Data: Size is equal to size returned from service
        val expectedSize = chartApiFake.getTopArtists().size
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