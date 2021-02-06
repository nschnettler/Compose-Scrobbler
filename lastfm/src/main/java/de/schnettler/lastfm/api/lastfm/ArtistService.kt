package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.ArtistTracksDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistService {

    @GET("?method=${LastFmService.METHOD_ARTIST_ALBUMS}&limit=10")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getArtistAlbums(
        @Query("artist") name: String
    ): List<AlbumDto>

    @GET("?method=${LastFmService.METHOD_ARTIST_TRACKS}&limit=5")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getArtistTracks(
        @Query("artist") name: String
    ): List<ArtistTracksDto>
}