package de.schnettler.scrobbler.charts.map

import de.schnettler.scrobbler.charts.model.ChartTrackResponse
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListTrack

object ChartTrackMapper : IndexedMapper<ChartTrackResponse, TopListTrack> {
    override suspend fun map(index: Int, from: ChartTrackResponse): TopListTrack {
        val track = TrackMapper.map(from, null)
        val toplist = createTopListEntry(track.id, EntityType.TRACK, ListType.CHART, index, from.playcount)
        return TopListTrack(toplist, track)
    }
}