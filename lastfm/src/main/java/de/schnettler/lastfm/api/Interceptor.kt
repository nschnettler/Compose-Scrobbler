package de.schnettler.lastfm.api

import okhttp3.Interceptor
import okhttp3.Response

const val USER_AGENT = "JAScrobbler/0.0.1 ( service.niklasschnettler@gmail.com )"

class UserAgentInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val newRequest = req.newBuilder()
            .header("User-Agent", USER_AGENT)
            .build()
        return chain.proceed(newRequest)
    }
}

class LastFMInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url= original.url.newBuilder()
            .addQueryParameter("api_key", LastFmService.API_KEY)
            .addQueryParameter("format", "json")
            .build()
        return chain.proceed(original.newBuilder().url(url).build())
    }
}