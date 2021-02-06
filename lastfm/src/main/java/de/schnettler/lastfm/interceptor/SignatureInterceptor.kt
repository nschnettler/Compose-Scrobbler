package de.schnettler.lastfm.interceptor

import de.schnettler.lastfm.createSignature
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class SignatureInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val parameterMap = original.url.queryParameterNames.filterNot { it == "format" }.associateWith {
            original.url.queryParameter(it).orEmpty()
        }.toMutableMap()

        val url = original.url.newBuilder()
            .addQueryParameter("api_sig", createSignature(parameterMap))
            .build()

        return chain.proceed(original.newBuilder().url(url).build())
    }
}