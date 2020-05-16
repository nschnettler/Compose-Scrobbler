package de.schnettler.lastfm.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.*
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"
        const val SECRET = "***REPLACE_WITH_LASTFM_SECRET***"

        const val METHOD_AUTH_SESSION = "auth.getSession"
        const val METHOD_USER_INFO = "user.getinfo"
        const val METHOD_USER_ARTISTS = "user.getTopArtists"
        const val METHOD_USER_ALBUMS = "user.getTopAlbums"
        const val METHOD_USER_TRACKS = "user.getTopTracks"
        const val METHOD_USER_RECENT = "user.getRecentTracks"
        const val METHOD_ARTIST_INFO = "artist.getInfo"
    }

    @GET("?method=chart.gettopartists&")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(): List<ArtistDto>


    @GET("?method=$METHOD_AUTH_SESSION")
    @Wrapped(path = ["session"])
    suspend fun getSession(
        @Query("token") token: String,
        @Query("api_sig") signature: String
    ): SessionDto

    @GET("?method=$METHOD_USER_INFO")
    @Wrapped(path = ["user"])
    suspend fun getUserInfo(
        @Query("sk") sessionKey: String
    ): UserDto

    @GET("?method=$METHOD_USER_ALBUMS&limit=5")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getUserTopAlbums(
        @Query("sk") sessionKey: String
    ): List<AlbumDto>

    @GET("?method=$METHOD_USER_ARTISTS&limit=5")
    @Wrapped(path = ["topartists", "artist"])
    suspend fun getUserTopArtists(
        @Query("sk") sessionKey: String
    ): List<ArtistDto>

    @GET("?method=$METHOD_USER_TRACKS&limit=5")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getUserTopTracks(
        @Query("sk") sessionKey: String
    ): List<TrackDto>

    @GET("?method=$METHOD_USER_RECENT")
    @Wrapped(path = ["recenttracks", "track"])
    suspend fun getUserRecentTrack(
        @Query("sk") sessionKey: String
    ): List<TrackWithAlbumDto>

    @GET("?method=$METHOD_ARTIST_INFO")
    @Wrapped(path = ["artist"])
    suspend fun getArtistInfo(
        @Query("artist") name: String
    ): ArtistInfoDto
}