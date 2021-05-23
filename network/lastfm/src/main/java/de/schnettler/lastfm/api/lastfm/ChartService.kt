package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.lastfm.models.UserTrackDto
import retrofit2.http.GET

interface ChartService {
    @GET("?method=chart.gettopartists")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(): List<ChartArtistDto>

    @GET("?method=chart.gettoptracks")
    @Wrapped(path = ["tracks", "track"])
    suspend fun getTopTracks(): List<UserTrackDto>
}