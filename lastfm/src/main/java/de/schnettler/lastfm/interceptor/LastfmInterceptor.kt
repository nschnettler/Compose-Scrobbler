package de.schnettler.lastfm.interceptor

import de.schnettler.lastfm.api.lastfm.LastFmService
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LastfmInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("api_key", LastFmService.API_KEY)
            .addQueryParameter("format", "json")
            .build()
        return chain.proceed(original.newBuilder().url(url).build())
    }
}