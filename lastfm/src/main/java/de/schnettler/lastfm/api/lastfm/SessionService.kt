package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.di.tag.SignatureAuthentication
import de.schnettler.lastfm.models.SessionDto
import retrofit2.http.GET
import retrofit2.http.Query

@SignatureAuthentication
interface SessionService {
    companion object {
        const val METHOD_AUTH_SESSION = "auth.getSession"
    }
    @GET("?method=$METHOD_AUTH_SESSION")
    @Wrapped(path = ["session"])
    suspend fun getSession(
        @Query("token") token: String,
    ): SessionDto
}