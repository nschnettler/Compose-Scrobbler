package de.schnettler.scrobbler.details.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.details.model.AlbumInfoResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import retrofit2.http.GET
import retrofit2.http.Query

@SessionAuthentication
interface AlbumApi {
    @GET("?method=album.getInfo")
    @Wrapped(path = ["album"])
    suspend fun getAlbumInfo(
        @Query("artist") name: String,
        @Query("album") albumName: String,
    ): AlbumInfoResponse
}