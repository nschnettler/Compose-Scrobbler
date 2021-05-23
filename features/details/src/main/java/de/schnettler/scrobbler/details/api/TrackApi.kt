package de.schnettler.scrobbler.details.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.details.model.TrackInfoResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.network.common.annotation.tag.SignatureAuthentication
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@SessionAuthentication
interface TrackApi {
    @GET("?method=track.getInfo")
    @Wrapped(path = ["track"])
    suspend fun getTrackInfo(
        @Query("artist") artistName: String,
        @Query("track") trackName: String,
    ): TrackInfoResponse
}

@SessionAuthentication
@SignatureAuthentication
interface TrackPostApi {
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