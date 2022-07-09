package de.schnettler.scrobbler.image.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.image.model.SpotifyArtist
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@SessionAuthentication
interface SpotifyApi {
    @GET("search?type=artist")
    @Wrapped(path = ["artists", "items"])
    suspend fun searchArtist(@Query("q") name: String): List<SpotifyArtist>

    @GET("artists/{id}")
    suspend fun getArtist(@Path("id") artistId: String): SpotifyArtist?
}