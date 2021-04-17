package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.TimePeriod
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.ResponseInfo
import de.schnettler.lastfm.models.UserArtistResponse
import de.schnettler.lastfm.models.UserDto
import de.schnettler.lastfm.models.UserTrackDto
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface UserService {
    companion object {
        const val METHOD_USER_INFO = "user.getinfo"
        const val METHOD_USER_ARTISTS = "user.getTopArtists"
        const val METHOD_USER_ALBUMS = "user.getTopAlbums"
        const val METHOD_USER_TRACKS = "user.getTopTracks"
        const val METHOD_USER_RECENT = "user.getRecentTracks"
        const val METHOD_USER_LOVED_TRACKS = "user.getLovedTracks"
    }

    @GET("?method=$METHOD_USER_INFO")
    @Wrapped(path = ["user"])
    suspend fun getUserInfo(): UserDto

    @GET("?method=$METHOD_USER_ALBUMS&limit=15")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getTopAlbums(
        @Query("period") timePeriod: TimePeriod,
    ): List<AlbumDto>

    @GET("?method=$METHOD_USER_ARTISTS&limit=15")
    @Wrapped(path = ["topartists"])
    suspend fun getTopArtists(
        @Query("period") timePeriod: TimePeriod
    ): UserArtistResponse

    @GET("?method=$METHOD_USER_LOVED_TRACKS&limit=1")
    @Wrapped(path = ["lovedtracks", "@attr"])
    suspend fun getUserLikedTracksAmount(): ResponseInfo

    @GET("?method=$METHOD_USER_TRACKS&limit=15")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getTopTracks(
        @Query("period") timePeriod: TimePeriod,
    ): List<UserTrackDto>

    @GET("?method=$METHOD_USER_RECENT")
    @Wrapped(path = ["recenttracks", "track"])
    suspend fun getUserRecentTrack(): List<RecentTracksDto>
}