package de.schnettler.lastfm.interceptor

import de.schnettler.common.BuildConfig
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject

class SignatureInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val methodClass = original.tag(Invocation::class.java)?.method()?.declaringClass
        if (methodClass?.isAnnotationPresent(SignatureAuthentication::class.java) == false) {
            Timber.e("${methodClass.simpleName} needs ${SignatureAuthentication::class.java.simpleName} tag")
            return chain.proceed(original)
        }

        val parameterMap = original.url.queryParameterNames.filterNot { it == "format" }.associateWith {
            original.url.queryParameter(it).orEmpty()
        }.toMutableMap()

        val url = original.url.newBuilder()
            .addQueryParameter("api_sig", createSignature(parameterMap))
            .build()

        return chain.proceed(original.newBuilder().url(url).build())
    }

    private fun createSignature(params: MutableMap<String, String>): String {
        params["api_key"] = BuildConfig.LASTFM_API_KEY
        val sorted = params.toSortedMap()
        val signature = StringBuilder()
        sorted.forEach { (key, value) ->
            signature.append(key)
            signature.append(value)
        }
        signature.append(BuildConfig.LASTFM_SECRET)
        return signature.toString().md5()
    }

    private fun String.md5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }
}