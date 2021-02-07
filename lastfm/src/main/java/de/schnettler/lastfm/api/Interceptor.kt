package de.schnettler.lastfm.api

import de.schnettler.lastfm.api.spotify.SpotifyService
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class SpotifyAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val newRequest = req.newBuilder()
            .header("Authorization", "Basic ${SpotifyService.AUTH_BASE64}")
            .build()
        return chain.proceed(newRequest)
    }
}

class AccessTokenInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (token == null) {
            chain.proceed(chain.request())
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }
}

val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)