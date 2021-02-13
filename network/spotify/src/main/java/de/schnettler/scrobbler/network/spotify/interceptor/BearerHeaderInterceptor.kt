package de.schnettler.scrobbler.network.spotify.interceptor

import de.schnettler.scrobbler.network.spotify.SpotifyAuthProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class BearerHeaderInterceptor @Inject constructor(private val provider: SpotifyAuthProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val token = runBlocking { provider.getAuthToken() }
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } catch (error: NullPointerException) {
            chain.proceed(chain.request())
        }
    }
}