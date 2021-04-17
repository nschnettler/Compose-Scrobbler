package de.schnettler.repo.mapping.track

import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.InfoMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.core.map.TrackMapper

object TrackInfoMapper : Mapper<TrackInfoDto, TrackWithStatsAndInfo> {
    override suspend fun map(from: TrackInfoDto): TrackWithStatsAndInfo {
        val album = from.album?.let { album -> AlbumMapper.map(album) }
        val track = TrackMapper.map(from, album)
        val stats = StatMapper.map(from, track.id)
        // Duration is in ms here.
        val info = InfoMapper.map(from.copy(duration = from.duration / 1000), track.id)
        return TrackWithStatsAndInfo(entity = track, stats = stats, info = info, album = album)
    }
}

object ScrobbleMapper : Mapper<RecentTracksDto, Scrobble> {
    override suspend fun map(from: RecentTracksDto) = Scrobble(
        name = from.name,
        artist = from.artist.name,
        album = from.album.name,
        duration = 1,
        timestamp = from.date?.uts ?: Long.MAX_VALUE,
        playedBy = "external",
        status = if (from.date != null) ScrobbleStatus.EXTERNAL else ScrobbleStatus.PLAYING
    )
}