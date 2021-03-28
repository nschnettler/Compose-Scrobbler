package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.Toplist
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.lastfm.models.UserTrackDto
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.TrackMapper
import de.schnettler.repo.mapping.createTopListEntry

object ChartArtistMapper : IndexedMapper<ChartArtistDto, Toplist> {
    override suspend fun map(index: Int, from: ChartArtistDto): Toplist {
        val artist = ArtistMapper.map(from)
        val toplist = createTopListEntry(artist.id, EntityType.ARTIST, ListType.CHART, index, from.listeners)
        return TopListArtist(toplist, artist)
    }
}

object ChartTrackMapper : IndexedMapper<UserTrackDto, Toplist> {
    override suspend fun map(index: Int, from: UserTrackDto): Toplist {
        val track = TrackMapper.map(from, null)
        val toplist = createTopListEntry(track.id, EntityType.TRACK, ListType.CHART, index, from.playcount)
        return TopListTrack(toplist, track)
    }
}