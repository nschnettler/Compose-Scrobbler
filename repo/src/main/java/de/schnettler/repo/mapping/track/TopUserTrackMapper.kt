package de.schnettler.repo.mapping.track

import de.schnettler.lastfm.models.UserTrackDto
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListTrack

object TopUserTrackMapper : IndexedMapper<UserTrackDto, TopListTrack> {
    override suspend fun map(index: Int, from: UserTrackDto): TopListTrack {
        val track = TrackMapper.map(from, null)
        val top = createTopListEntry(track.id, EntityType.TRACK, ListType.USER, index, from.playcount)
        return TopListTrack(top, track)
    }
}