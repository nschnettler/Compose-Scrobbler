package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.lastfm.models.SingleScrobbleResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

@SessionAuthentication
@SignatureAuthentication
interface PostService {
    @POST("?method=track.scrobble")
    @Wrapped(path = ["scrobbles"])
    suspend fun submitScrobble(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("duration") duration: String,
        @Query("timestamp") timestamp: String,
    ): Response<SingleScrobbleResponse>

    @POST("?method=track.updateNowPlaying")
    @Wrapped(path = ["nowplaying"])
    suspend fun submitNowPlaying(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("duration") duration: String,
    ): Response<ScrobbleResponse>

    @POST("?method=track.scrobble")
    @Wrapped(path = ["scrobbles"])
    suspend fun submitMultipleScrobbles(
        @QueryMap body: Map<String, String>
    ): Response<MutlipleScrobblesResponse>

    @POST("?method=track.love")
    suspend fun loveTrack(
        @Query("track") track: String,
        @Query("artist") artist: String,
    ): Response<Any>

    @POST("?method=track.unlove")
    suspend fun unloveTrack(
        @Query("track") track: String,
        @Query("artist") artist: String,
    ): Response<Any>
}