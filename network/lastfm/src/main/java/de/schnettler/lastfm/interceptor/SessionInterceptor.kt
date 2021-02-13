package de.schnettler.lastfm.interceptor

import de.schnettler.lastfm.LastFmAuthProvider
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import timber.log.Timber
import javax.inject.Inject

class SessionInterceptor @Inject constructor(private val authProvider: LastFmAuthProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val session = runBlocking { authProvider.getSessionKey() }

        val methodClass = original.tag(Invocation::class.java)?.method()?.declaringClass
        if (methodClass?.isAnnotationPresent(SessionAuthentication::class.java) == false) {
            Timber.e("${methodClass.simpleName} needs ${SessionAuthentication::class.java.simpleName} tag")
            return chain.proceed(original)
        }
        val url = original.url.newBuilder()
            .addQueryParameter("sk", session)
            .build()

        return chain.proceed(original.newBuilder().url(url).build())
    }
}