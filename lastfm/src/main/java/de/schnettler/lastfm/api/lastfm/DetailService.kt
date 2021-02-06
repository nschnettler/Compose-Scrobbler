package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.di.tag.SessionAuthentication
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.lastfm.models.TrackInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface DetailService {
    companion object {
        const val METHOD_TRACK_INFO = "track.getInfo"
        const val METHOD_ALBUM_INFO = "album.getInfo"
        const val METHOD_ARTIST_INFO = "artist.getInfo"
    }

    @GET("?method=$METHOD_ARTIST_INFO")
    @Wrapped(path = ["artist"])
    suspend fun getArtistInfo(
        @Query("artist") name: String,
    ): ArtistInfoDto

    @GET("?method=$METHOD_TRACK_INFO")
    @Wrapped(path = ["track"])
    suspend fun getTrackInfo(
        @Query("artist") artistName: String,
        @Query("track") trackName: String,
    ): TrackInfoDto

    @GET("?method=$METHOD_ALBUM_INFO")
    @Wrapped(path = ["album"])
    suspend fun getAlbumInfo(
        @Query("artist") name: String,
        @Query("album") albumName: String,
    ): AlbumInfoDto
}