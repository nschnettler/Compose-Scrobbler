package de.schnettler.lastfm.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitService {
    val lastFmService: LastFmService by lazy {
        provideRetrofit(
            provideOkHttpClient(), LastFmService.ENDPOINT
        ).create(
            LastFmService::class.java)
    }
}

fun provideOkHttpClient(vararg interceptor: Interceptor): OkHttpClient = OkHttpClient().newBuilder()
    .apply { interceptor.forEach { addInterceptor(it) } }
    //.addNetworkInterceptor(StethoInterceptor())
    .build()

fun provideRetrofit(okHttpClient: OkHttpClient, endpoint: String): Retrofit = Retrofit.Builder()
    .baseUrl(endpoint)
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        )
    )
    .build()