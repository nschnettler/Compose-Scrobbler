package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.AlbumInfoDto
import javax.inject.Inject

class AlbumInfoMapper @Inject constructor() : Mapper<AlbumInfoDto, AlbumWithStatsAndInfo> {
    override suspend fun map(from: AlbumInfoDto): AlbumWithStatsAndInfo {
        val album = Album(
            name = from.name,
            url = from.url,
            artist = from.artist,
            imageUrl = from.image.lastOrNull()?.url
        )
        val info = EntityInfo(
            id = album.id,
            tags = from.tags.tag.map { tag -> tag.name },
            wiki = from.wiki?.summary ?: ""
        )
        val stats = Stats(
            id = album.id,
            plays = from.playcount,
            listeners = from.listeners,
            userPlays = from.userplaycount
        )
        val result = AlbumWithStatsAndInfo(entity = album, stats = stats, info = info)
        val tracks = from.tracks.track.map {
            val track = LastFmEntity.Track(
                name = it.name,
                url = it.url,
                artist = it.artist.name,
                album = album.name,
                imageUrl = album.imageUrl
            )
            val info = EntityInfo(
                id = track.id,
                duration = it.duration,
                wiki = ""
            )
            EntityWithInfo.TrackWithInfo(track, info)
        }
        result.tracks = tracks
        return result
    }
}