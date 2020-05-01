package de.schnettler.lastfm.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

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