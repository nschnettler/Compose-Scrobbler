package de.schnettler.repo.mapping.track

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListTrack
import de.schnettler.lastfm.models.UserTrackDto
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.TrackMapper
import de.schnettler.repo.mapping.createTopListEntry

object TopUserTrackMapper : IndexedMapper<UserTrackDto, TopListTrack> {
    override suspend fun map(index: Int, from: UserTrackDto): TopListTrack {
        val track = TrackMapper.map(from, null)
        val top = createTopListEntry(track.id, EntityType.TRACK, ListType.USER, index, from.playcount)
        return TopListTrack(top, track)
    }
}