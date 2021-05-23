package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.ArtistTracksDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistService {
    @GET("?method=artist.getTopAlbums")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getArtistAlbums(
        @Query("artist") name: String,
        @Query("limit") limit: Int = 10,
    ): List<AlbumDto>

    @GET("?method=artist.getTopTracks")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getArtistTracks(
        @Query("artist") name: String,
        @Query("limit") limit: Int = 5,
    ): List<ArtistTracksDto>
}