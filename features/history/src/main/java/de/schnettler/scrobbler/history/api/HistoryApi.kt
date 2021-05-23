package de.schnettler.scrobbler.history.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.scrobbler.history.model.RecentTrackResponse
import de.schnettler.scrobbler.network.common.annotation.tag.SessionAuthentication
import retrofit2.http.GET

@SessionAuthentication
interface HistoryApi {

    @GET("?method=user.getRecentTracks")
    @Wrapped(path = ["recenttracks", "track"])
    suspend fun getUserRecentTrack(): List<RecentTrackResponse>
}