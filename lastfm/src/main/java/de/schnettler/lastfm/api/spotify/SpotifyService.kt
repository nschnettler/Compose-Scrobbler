package de.schnettler.lastfm.api.spotify

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.encodeBase64
import de.schnettler.lastfm.models.SpotifyAccessTokenDto
import de.schnettler.lastfm.models.SpotifyArtist
import retrofit2.http.*

interface SpotifyService {
    companion object {
        const val ENDPOINT = "https://api.spotify.com/v1/"

        const val CLIENT_ID = "***REPLACE_WITH_SPOTIFY_CLIENT***"
        const val CLIENT_SECRET = "***REPLACE_WITH_SPOTIFY_SECRET***"
        const val TYPE_CLIENT = "client_credentials"
        val AUTH_BASE64 = "$CLIENT_ID:$CLIENT_SECRET".encodeBase64()
    }

    @GET("search?type=artist")
    @Wrapped(path = ["artists", "items"])
    suspend fun searchArtist(@Query("q") name: String): List<SpotifyArtist>
}

interface SpotifyAuthService {
    companion object {
        const val AUTH_ENDPOINT = "https://accounts.spotify.com/api/"
    }

    @POST("token")
    @FormUrlEncoded
    suspend fun login(@Field("grant_type") type: String): SpotifyAccessTokenDto
}