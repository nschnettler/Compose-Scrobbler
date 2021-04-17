package de.schnettler.scrobbler.history.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.history.model.RecentTrackResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import retrofit2.http.GET

@SessionAuthentication
interface HistoryApi {
    companion object {
        const val METHOD_USER_RECENT = "user.getRecentTracks"
    }

    @GET("?method=$METHOD_USER_RECENT")
    @Wrapped(path = ["recenttracks", "track"])
    suspend fun getUserRecentTrack(): List<RecentTrackResponse>
}