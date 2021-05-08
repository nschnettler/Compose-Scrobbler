package de.schnettler.scrobbler.profile.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.model.TimePeriod
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.scrobbler.profile.model.remote.ResponseInfo
import de.schnettler.scrobbler.profile.model.remote.TopAlbumResponse
import de.schnettler.scrobbler.profile.model.remote.TopArtistListResponse
import de.schnettler.scrobbler.profile.model.remote.TopTrackResponse
import de.schnettler.scrobbler.profile.model.remote.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface ProfileApi {

    @GET("?method=user.getinfo")
    @Wrapped(path = ["user"])
    suspend fun getUserInfo(): UserInfoResponse

    @GET("?method=user.getTopAlbums&limit=15")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getTopAlbums(
        @Query("period") timePeriod: TimePeriod,
    ): List<TopAlbumResponse>

    @GET("?method=user.getTopArtists&limit=15")
    @Wrapped(path = ["topartists"])
    suspend fun getTopArtists(
        @Query("period") timePeriod: TimePeriod
    ): TopArtistListResponse

    @GET("?method=user.getLovedTracks&limit=1")
    @Wrapped(path = ["lovedtracks", "@attr"])
    suspend fun getUserLikedTracksAmount(): ResponseInfo

    @GET("?method=user.getTopTracks&limit=15")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getTopTracks(
        @Query("period") timePeriod: TimePeriod,
    ): List<TopTrackResponse>
}