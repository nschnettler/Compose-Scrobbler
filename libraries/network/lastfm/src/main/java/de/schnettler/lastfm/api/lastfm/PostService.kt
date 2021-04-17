package de.schnettler.lastfm.api.lastfm

import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

@SessionAuthentication
@SignatureAuthentication
interface PostService {


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