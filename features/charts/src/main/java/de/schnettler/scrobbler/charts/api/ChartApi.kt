package de.schnettler.scrobbler.charts.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.charts.model.ChartTrackResponse
import retrofit2.http.GET

interface ChartApi {
    @GET("?method=chart.gettopartists")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(): List<ChartArtistResponse>

    @GET("?method=chart.gettoptracks")
    @Wrapped(path = ["tracks", "track"])
    suspend fun getTopTracks(): List<ChartTrackResponse>
}