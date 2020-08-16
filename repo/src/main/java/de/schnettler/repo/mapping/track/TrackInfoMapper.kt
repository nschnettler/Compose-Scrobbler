package de.schnettler.repo.mapping.track

import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.repo.mapping.BaseAlbumMapper
import de.schnettler.repo.mapping.BaseInfoMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.BaseTrackMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class TrackInfoMapper @Inject constructor() : Mapper<TrackInfoDto, TrackWithStatsAndInfo> {
    override suspend fun map(from: TrackInfoDto): TrackWithStatsAndInfo {
        val album = from.album?.let { album -> BaseAlbumMapper.map(album) }
        val track = BaseTrackMapper.map(from, album)
        val stats = BaseStatMapper.map(from, track.id)
        val info = BaseInfoMapper.map(from, track.id)
        return TrackWithStatsAndInfo(entity = track, stats = stats, info = info, album = album)
    }
}

fun RecentTracksDto.mapToLocal() = Scrobble(
    name = name,
    artist = artist.name,
    album = album.name,
    duration = 1,
    timestamp = date?.uts ?: -1,
    playedBy = "external",
    status = if (date != null) ScrobbleStatus.EXTERNAL else ScrobbleStatus.PLAYING
)