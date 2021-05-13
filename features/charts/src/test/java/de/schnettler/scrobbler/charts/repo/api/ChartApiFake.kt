package de.schnettler.scrobbler.charts.repo.api

import de.schnettler.scrobbler.charts.api.ChartApi
import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.charts.model.ChartTrackResponse
import de.schnettler.scrobbler.charts.repo.tools.ApiTestUtils

class ChartApiFake : ChartApi, ApiTestUtils() {

    override suspend fun getTopArtists(): List<ChartArtistResponse> {
        return moshi.parseJsonList(getJsonStringFromFile("topArtists")) ?: emptyList()
    }

    override suspend fun getTopTracks(): List<ChartTrackResponse> {
        TODO("Not yet implemented")
    }
}