package de.schnettler.lastfm.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.ArtistDto
import retrofit2.http.GET

interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"
        const val SECRET = "***REPLACE_WITH_LASTFM_SECRET***"
    }

    @GET("?method=chart.gettopartists&format=json&api_key=$API_KEY")
    @Wrapped(path = ["artists" , "artist"])
    suspend fun getTopArtists(): List<ArtistDto>
}