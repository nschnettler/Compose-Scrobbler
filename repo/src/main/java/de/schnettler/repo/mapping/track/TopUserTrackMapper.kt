package de.schnettler.repo.mapping.track

import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.lastfm.models.UserTrackDto
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType

object TopUserTrackMapper : IndexedMapper<UserTrackDto, TopListTrack> {
    override suspend fun map(index: Int, from: UserTrackDto): TopListTrack {
        val track = TrackMapper.map(from, null)
        val top = createTopListEntry(track.id, EntityType.TRACK, ListType.USER, index, from.playcount)
        return de.schnettler.scrobbler.model.TopListTrack(top, track)
    }
}