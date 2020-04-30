package de.schnettler.lastfm.api

import de.schnettler.lastfm.models.TopArtistsResponse
import retrofit2.http.GET

interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"
        const val SECRET = "***REPLACE_WITH_LASTFM_SECRET***"
    }

    /*
     * Trending Shows
     */
    @GET("?method=chart.gettopartists&format=json&api_key=$API_KEY")
    suspend fun getTopArtists(): TopArtistsResponse
}