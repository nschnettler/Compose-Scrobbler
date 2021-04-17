package de.schnettler.scrobbler.network.spotify.api

import de.schnettler.scrobbler.network.common.annotation.tag.BasicAuthentication
import de.schnettler.scrobbler.network.spotify.models.SpotifyTokenDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@BasicAuthentication
interface SpotifyLoginService {
    @POST("token")
    @FormUrlEncoded
    suspend fun login(@Field("grant_type") type: String): SpotifyTokenDto
}