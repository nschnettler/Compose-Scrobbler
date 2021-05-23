package de.schnettler.scrobbler.search.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.search.model.SearchResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("?method=artist.search")
    @Wrapped(path = ["results", "artistmatches", "artist"])
    suspend fun searchArtist(
        @Query("artist") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultResponse>

    @GET("?method=album.search")
    @Wrapped(path = ["results", "albummatches", "album"])
    suspend fun searchAlbum(
        @Query("album") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultResponse>

    @GET("?method=track.search")
    @Wrapped(path = ["results", "trackmatches", "track"])
    suspend fun searchTrack(
        @Query("track") query: String,
        @Query("limit") limit: Long = 30
    ): List<SearchResultResponse>
}