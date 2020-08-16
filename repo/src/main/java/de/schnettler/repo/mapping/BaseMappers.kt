package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.Stats
import de.schnettler.database.models.TopListEntry
import de.schnettler.lastfm.models.BaseAlbumDto
import de.schnettler.lastfm.models.BaseArtistDto
import de.schnettler.lastfm.models.BaseInfoDto
import de.schnettler.lastfm.models.BaseStatsDto
import de.schnettler.lastfm.models.BaseTrackDto

object AlbumMapper : Mapper<BaseAlbumDto, LastFmEntity.Album> {
    override suspend fun map(from: BaseAlbumDto) = LastFmEntity.Album(
        name = from.name,
        url = from.url,
        artist = from.artist,
        imageUrl = from.image.lastOrNull()?.url
    )
}

object ArtistMapper : Mapper<BaseArtistDto, LastFmEntity.Artist> {
    override suspend fun map(from: BaseArtistDto) = LastFmEntity.Artist(
        name = from.name,
        url = from.url
    )
}

object TrackMapper : ParameterMapper<BaseTrackDto, LastFmEntity.Track, LastFmEntity.Album?> {
    override suspend fun map(from: BaseTrackDto, album: LastFmEntity.Album?): LastFmEntity.Track =
        LastFmEntity.Track(
            name = from.name,
            url = from.url,
            artist = from.artist.name,
            album = album?.name,
            albumId = album?.id,
            imageUrl = album?.imageUrl
        )
}

object StatMapper : ParameterMapper<BaseStatsDto, Stats, String> {
    override suspend fun map(from: BaseStatsDto, parameter: String) = Stats(
        id = parameter,
        plays = from.playcount,
        listeners = from.listeners,
        userPlays = from.userplaycount
    )
}

object InfoMapper : ParameterMapper<BaseInfoDto, EntityInfo, String> {
    override suspend fun map(from: BaseInfoDto, id: String) = EntityInfo(
        id = id,
        tags = from.tags?.tag?.map { tag -> tag.name } ?: emptyList(),
        duration = from.duration,
        wiki = from.wiki?.summary
    )
}

fun createTopListEntry(id: String, type: EntityType, listType: ListType, index: Int, count: Long?) = TopListEntry(
    id = id,
    entityType = type,
    listType = listType,
    index = index,
    count = count ?: 0
)