package de.schnettler.lastfm.api.lastfm

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("?method=artist.search")
    @Wrapped(path = ["results", "artistmatches", "artist"])
    suspend fun searchArtist(
        @Query("artist") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultDto>

    @GET("?method=album.search")
    @Wrapped(path = ["results", "albummatches", "album"])
    suspend fun searchAlbum(
        @Query("album") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultDto>

    @GET("?method=track.search")
    @Wrapped(path = ["results", "trackmatches", "track"])
    suspend fun searchTrack(
        @Query("track") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultDto>
}