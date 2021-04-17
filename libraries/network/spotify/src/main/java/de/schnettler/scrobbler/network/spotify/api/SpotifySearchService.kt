package de.schnettler.scrobbler.network.spotify.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.BuildConfig
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.network.spotify.models.SpotifyArtist
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface SpotifySearchService {
    companion object {
        const val AUTH_BASE64 = BuildConfig.SPOTIFY_AUTH
        const val TYPE_CLIENT = "client_credentials"
    }

    @GET("search?type=artist")
    @Wrapped(path = ["artists", "items"])
    suspend fun searchArtist(@Query("q") name: String): List<SpotifyArtist>
}