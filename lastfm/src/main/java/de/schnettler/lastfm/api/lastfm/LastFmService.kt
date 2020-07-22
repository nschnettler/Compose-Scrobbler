package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.common.BuildConfig
import de.schnettler.common.TimePeriod
import de.schnettler.lastfm.models.*
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = BuildConfig.LASTFM_API_KEY
        const val SECRET = BuildConfig.LASTFM_SECRET

        const val METHOD_AUTH_SESSION = "auth.getSession"
        const val METHOD_USER_INFO = "user.getinfo"
        const val METHOD_USER_ARTISTS = "user.getTopArtists"
        const val METHOD_USER_ALBUMS = "user.getTopAlbums"
        const val METHOD_USER_TRACKS = "user.getTopTracks"
        const val METHOD_USER_RECENT = "user.getRecentTracks"
        const val METHOD_USER_LOVED_TRACKS = "user.getLovedTracks"
        const val METHOD_ARTIST_INFO = "artist.getInfo"
        const val METHOD_ARTIST_ALBUMS = "artist.getTopAlbums"
        const val METHOD_ARTIST_TRACKS = "artist.getTopTracks"
        const val METHOD_TRACK_INFO = "track.getInfo"
        const val METHOD_ALBUM_INFO = "album.getInfo"
        const val METHOD_SCROBBLE = "track.scrobble"
        const val METHOD_NOWPLAYING = "track.updateNowPlaying"
    }

    @GET("?method=chart.gettopartists")
    @Wrapped(path = ["artists", "artist"])
    suspend fun getTopArtists(): List<ChartArtistDto>


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

    @GET("?method=$METHOD_USER_ALBUMS&limit=10")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getUserTopAlbums(
        @Query("period") timePeriod: TimePeriod,
        @Query("sk") sessionKey: String
    ): List<AlbumDto>

    @GET("?method=$METHOD_USER_ARTISTS&limit=10")
    @Wrapped(path = ["topartists"])
    suspend fun getUserTopArtists(
        @Query("period") timePeriod: TimePeriod,
        @Query("sk") sessionKey: String
    ): UserArtistResponse

    @GET("?method=$METHOD_USER_LOVED_TRACKS&limit=5")
    @Wrapped(path = ["lovedtracks"])
    suspend fun getUserLikedTracks(
        @Query("sk") sessionKey: String
    ): LovedTracksResponse

    @GET("?method=$METHOD_USER_TRACKS&limit=10")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getUserTopTracks(
        @Query("period") timePeriod: TimePeriod,
        @Query("sk") sessionKey: String
    ): List<UserTrackDto>

    @GET("?method=$METHOD_USER_RECENT")
    @Wrapped(path = ["recenttracks", "track"])
    suspend fun getUserRecentTrack(
        @Query("sk") sessionKey: String
    ): List<RecentTracksDto>

    @GET("?method=$METHOD_ARTIST_INFO")
    @Wrapped(path = ["artist"])
    suspend fun getArtistInfo(
        @Query("artist") name: String,
        @Query("sk") sessionKey: String
    ): ArtistInfoDto

    @GET("?method=$METHOD_TRACK_INFO")
    @Wrapped(path = ["track"])
    suspend fun getTrackInfo(
        @Query("artist") artistName: String,
        @Query("track") trackName: String,
        @Query("sk") sessionKey: String
    ): TrackInfoDto

    @GET("?method=$METHOD_ARTIST_ALBUMS&limit=10")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getArtistAlbums(
        @Query("artist") name: String
    ): List<AlbumDto>

    @GET("?method=$METHOD_ARTIST_TRACKS&limit=5")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getArtistTracks(
        @Query("artist") name: String
    ): List<ArtistTracksDto>

    @GET("?method=$METHOD_ALBUM_INFO")
    @Wrapped(path = ["album"])
    suspend fun getAlbumInfo(
        @Query("artist") artistName: String,
        @Query("album") albumName: String,
        @Query("sk") sessionKey: String
    ): AlbumInfoDto

    @GET("?method=artist.search")
    @Wrapped(path = ["results", "artistmatches", "artist" ])
    suspend fun searchArtist(
        @Query("artist") query: String,
        @Query("limit") limit: Long = 30
    ): List<ChartArtistDto>
}