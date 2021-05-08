package de.schnettler.scrobbler.authentication.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.authentication.model.SessionResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import retrofit2.http.GET
import retrofit2.http.Query

@SignatureAuthentication
interface LastfmSessionApi {

    @GET("?method=auth.getSession")
    @Wrapped(path = ["session"])
    suspend fun getSession(
        @Query("token") token: String,
    ): SessionResponse
}