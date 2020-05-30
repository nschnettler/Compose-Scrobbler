package de.schnettler.lastfm.api

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitService {
    val lastFmService: LastFmService by lazy {
        provideRetrofit(
            provideOkHttpClient(LastFMInterceptor()), LastFmService.ENDPOINT
        ).create(
            LastFmService::class.java)
    }

    val spotifyAuthService: SpotifyService by lazy {
        provideRetrofit(
            provideOkHttpClient(SpotifyAuthInterceptor()), SpotifyService.AUTH_ENDPOINT
        ).create(
            SpotifyService::class.java
        )
    }

    fun provideAuthenticatedSpotifyService(token: String, authenticator: Authenticator): SpotifyService =
        provideRetrofit(
            provideOkHttpClient(AccessTokenInterceptor(token), auth = authenticator), SpotifyService.ENDPOINT
        ).create(SpotifyService::class.java)

}

fun provideOkHttpClient(vararg interceptor: Interceptor, auth: Authenticator? = null): OkHttpClient = OkHttpClient().newBuilder()
    .apply { interceptor.forEach { addInterceptor(it) } }
    .apply { auth?.let { authenticator(auth) } }
    .build()

fun provideRetrofit(okHttpClient: OkHttpClient, endpoint: String): Retrofit = Retrofit.Builder()
    .baseUrl(endpoint)
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder()
                .add(Wrapped.ADAPTER_FACTORY)
                .add(KotlinJsonAdapterFactory())
                .build()
        )
    )
    .build()