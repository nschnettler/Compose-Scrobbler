package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.di.tag.SessionAuthentication
import de.schnettler.lastfm.di.tag.SignatureAuthentication
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.lastfm.models.SingleScrobbleResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Query

@SessionAuthentication
@SignatureAuthentication
interface PostService {
    companion object {
        const val METHOD_SCROBBLE = "track.scrobble"
        const val METHOD_NOWPLAYING = "track.updateNowPlaying"
        const val METHOD_LOVE = "track.love"
        const val METHOD_UNLOVE = "track.unlove"
    }
    @POST("?method=$METHOD_SCROBBLE")
    @Wrapped(path = ["scrobbles"])
    suspend fun submitScrobble(
        @Field("track") track: String,
        @Field("artist") artist: String,
        @Field("album") album: String,
        @Field("duration") duration: String,
        @Field("timestamp") timestamp: String,
    ): Response<SingleScrobbleResponse>

    @POST("?method=$METHOD_NOWPLAYING")
    @Wrapped(path = ["nowplaying"])
    suspend fun submitNowPlaying(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("duration") duration: String,
    ): Response<ScrobbleResponse>

    @POST(LastFmService.ENDPOINT)
    @Wrapped(path = ["scrobbles"])
    suspend fun submitMultipleScrobbles(
        @Field("track") track: String,
        @Field("artist") artist: String,
        @Field("album") album: String,
        @Field("duration") duration: String,
        @Field("timestamp") timestamp: String,
    ): Response<MutlipleScrobblesResponse>

    @POST(LastFmService.ENDPOINT)
    suspend fun toggleTrackLoveStatus(
        @Query("method") method: String,
        @Query("track") track: String,
        @Query("artist") artist: String,
    ): Response<Any>
}