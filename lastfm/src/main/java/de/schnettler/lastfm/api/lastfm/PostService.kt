package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.BuildConfig
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.lastfm.models.SingleScrobbleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PostService {
    companion object {
        const val METHOD_SCROBBLE = "track.scrobble"
        const val METHOD_NOWPLAYING = "track.updateNowPlaying"
        const val METHOD_LOVE = "track.love"
        const val METHOD_UNLOVE = "track.unlove"
    }
    @POST(LastFmService.ENDPOINT)
    @FormUrlEncoded
    @Wrapped(path = ["scrobbles"])
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
    ): Response<SingleScrobbleResponse>

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
    ): Response<ScrobbleResponse>

    @POST(LastFmService.ENDPOINT)
    @Wrapped(path = ["scrobbles"])
    suspend fun submitMultipleScrobbles(
        @Body body: String
    ): Response<MutlipleScrobblesResponse>

    @POST(LastFmService.ENDPOINT)
    @FormUrlEncoded
    suspend fun toggleTrackLoveStatus(
        @Field("api_key") apiKey: String = BuildConfig.LASTFM_API_KEY,
        @Field("method") method: String = METHOD_LOVE,
        @Field("track") track: String,
        @Field("artist") artist: String,
        @Field("sk") sessionKey: String,
        @Field("api_sig") signature: String,
        @Field("format") format: String = "json"
    ): Response<Any>
}