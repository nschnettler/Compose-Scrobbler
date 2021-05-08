package de.schnettler.scrobbler.details.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.details.model.ArtistAlbumResponse
import de.schnettler.scrobbler.details.model.ArtistInfoResponse
import de.schnettler.scrobbler.details.model.ArtistTrackResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface ArtistApi {
    @GET("?method=artist.getTopAlbums")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getArtistAlbums(
        @Query("artist") name: String,
        @Query("limit") limit: Int = 10,
    ): List<ArtistAlbumResponse>

    @GET("?method=artist.getTopTracks")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getArtistTracks(
        @Query("artist") name: String,
        @Query("limit") limit: Int = 5,
    ): List<ArtistTrackResponse>

    @GET("?method=artist.getInfo")
    @Wrapped(path = ["artist"])
    suspend fun getArtistInfo(
        @Query("artist") name: String,
    ): ArtistInfoResponse
}