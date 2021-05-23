package de.schnettler.scrobbler.profile.map

import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.profile.model.remote.TopTrackResponse

object TopUserTrackMapper : IndexedMapper<TopTrackResponse, TopListTrack> {
    override suspend fun map(index: Int, from: TopTrackResponse): TopListTrack {
        val track = TrackMapper.map(from, null)
        val top = createTopListEntry(track.id, EntityType.TRACK, ListType.USER, index, from.playcount)
        return TopListTrack(top, track)
    }
}