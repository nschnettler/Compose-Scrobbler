package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.BuildConfig
import de.schnettler.lastfm.models.NowPlayingResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ScrobblerService {
    @POST(LastFmService.ENDPOINT)
    @FormUrlEncoded
    suspend fun submitScrobble(
            @Field("api_key") apiKey: String = BuildConfig.LASTFM_API_KEY,
            @Field("method") method: String,
            @Field("track") track: String,
            @Field("artist") artist: String,
            @Field("album") album: String,
            @Field("duration") duration: String,
            @Field("timestamp") timestamp: String,
            @Field("sk") sessionKey: String,
            @Field("api_sig") signature: String,
            @Field("format") format: String = "json"
    ): Response<String>

    @POST(LastFmService.ENDPOINT)
    @Wrapped(path = ["nowplaying"])
    @FormUrlEncoded
    suspend fun submitNowPlaying(
            @Field("api_key") apiKey: String = BuildConfig.LASTFM_API_KEY,
            @Field("method") method: String,
            @Field("track") track: String,
            @Field("artist") artist: String,
            @Field("album") album: String,
            @Field("duration") duration: String,
            @Field("sk") sessionKey: String,
            @Field("api_sig") signature: String,
            @Field("format") format: String = "json"
    ): Response<NowPlayingResponse>
}