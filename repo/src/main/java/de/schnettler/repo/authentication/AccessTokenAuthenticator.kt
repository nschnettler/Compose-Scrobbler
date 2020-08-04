package de.schnettler.repo.authentication

import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AccessTokenAuthenticator @Inject constructor(
    private val provider: SpotifyAuthProvider
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        println("Detected authentication error ${response.code} on ${response.request.url}")

        when (hasBearerAuthorizationToken(
            response
        )) {
            false -> println("No Token to refresh")
            true -> {
                var currentToken = runBlocking(GlobalScope.coroutineContext) { provider.getToken() }

                if (currentToken.token == response.request.headers["Authorization"]?.removePrefix("Bearer ")) {
                    println("Request new Token")
                    currentToken = runBlocking(GlobalScope.coroutineContext) { provider.refreshToken() }
                }

                val request = response.request
                    .newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer ${currentToken.token}")
                    .build()
                println("Retrying with ${request.headers}")
                return request
            }
        }
        return null
    }
}

private fun hasBearerAuthorizationToken(response: Response?): Boolean =
    response?.request?.header("Authorization")?.startsWith("Bearer ") ?: false