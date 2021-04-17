package de.schnettler.repo.mapping.album

import de.schnettler.scrobbler.core.model.EntityWithInfo
import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.InfoMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.core.map.TrackMapper

object AlbumInfoMapper : Mapper<AlbumInfoDto, AlbumDetails> {
    override suspend fun map(from: AlbumInfoDto): AlbumDetails {
        val album = AlbumMapper.map(from)
        val info = InfoMapper.map(from, album.id)
        val stats = StatMapper.map(from, album.id)
        val result = AlbumDetails(entity = album, stats = stats, info = info, artist = null)
        val tracks = from.tracks.track.map {
            val track = TrackMapper.map(it, album)
            EntityWithInfo.TrackWithInfo(track, InfoMapper.map(it, track.id))
        }
        result.tracks = tracks
        return result
    }
}