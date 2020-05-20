package de.schnettler.repo

import de.schnettler.database.models.AuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AccessTokenAuthenticator(val provider: AccessTokenProvider, val scope: CoroutineScope): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        println("Started OkHttp Authentication")
        val token = provider.getToken()

        synchronized(this) {
            val newToken = provider.getToken()

            // Check if the request made was previously made as an authenticated request.
            if (response.request.header("Authorization") != null) {
                // If the token has changed since the request was made, use the new token.
                if (newToken != token) {
                    return response.request
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()
                }

                //Request new Token
                var updatedToken: AuthToken? = null
                scope.launch {
                    updatedToken = provider.refreshToken()
                }

                updatedToken?.let {
                    return response.request
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $updatedToken")
                        .build()
                }
            }
        }
        return null
    }
}