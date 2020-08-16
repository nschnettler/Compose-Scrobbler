package de.schnettler.repo.mapping.track

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.repo.mapping.BaseAlbumMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.BaseTrackMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class TrackMapper @Inject constructor() : Mapper<TrackInfoDto, TrackWithStatsAndInfo> {
    override suspend fun map(from: TrackInfoDto): TrackWithStatsAndInfo {
        val imageUrl = from.album?.image?.lastOrNull()?.url
        val album = from.album?.let { album -> BaseAlbumMapper.map(album) }
        val track = BaseTrackMapper.map(from).copy(albumId = album?.id, imageUrl = imageUrl)
        val stats = BaseStatMapper.map(from).copy(id = track.id)
        val info = EntityInfo(
            id = track.id,
            tags = from.toptags.tag.map { tag -> tag.name },
            wiki = from.wiki?.content ?: from.wiki?.summary
        )
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