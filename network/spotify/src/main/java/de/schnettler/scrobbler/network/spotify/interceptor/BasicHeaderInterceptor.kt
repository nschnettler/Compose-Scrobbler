package de.schnettler.scrobbler.network.spotify.interceptor

import de.schnettler.scrobbler.network.spotify.api.SpotifySearchService
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BasicHeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val newRequest = req.newBuilder()
            .header("Authorization", "Basic ${SpotifySearchService.AUTH_BASE64}")
            .build()
        return chain.proceed(newRequest)
    }
}