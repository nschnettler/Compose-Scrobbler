package de.schnettler.repo.mapping.artist

import de.schnettler.scrobbler.core.model.TopListArtist
import de.schnettler.scrobbler.core.model.TopListTrack
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.lastfm.models.UserTrackDto
import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.core.model.EntityType
import de.schnettler.scrobbler.core.model.ListType

object ChartArtistMapper : IndexedMapper<ChartArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: ChartArtistDto): TopListArtist {
        val artist = ArtistMapper.map(from)
        val toplist = createTopListEntry(artist.id, EntityType.ARTIST, ListType.CHART, index, from.listeners)
        return TopListArtist(toplist, artist)
    }
}

object ChartTrackMapper : IndexedMapper<UserTrackDto, TopListTrack> {
    override suspend fun map(index: Int, from: UserTrackDto): TopListTrack {
        val track = TrackMapper.map(from, null)
        val toplist = createTopListEntry(track.id, EntityType.TRACK, ListType.CHART, index, from.playcount)
        return TopListTrack(toplist, track)
    }
}