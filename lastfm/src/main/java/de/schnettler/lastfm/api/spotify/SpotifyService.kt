package de.schnettler.lastfm.api.spotify

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.BuildConfig
import de.schnettler.lastfm.models.SpotifyTokenDto
import de.schnettler.lastfm.models.SpotifyArtist
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyService {
    companion object {
        const val ENDPOINT = "https://api.spotify.com/v1/"

        const val AUTH_BASE64 = BuildConfig.SPOTIFY_AUTH
        const val TYPE_CLIENT = "client_credentials"
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
    suspend fun login(@Field("grant_type") type: String): SpotifyTokenDto
}