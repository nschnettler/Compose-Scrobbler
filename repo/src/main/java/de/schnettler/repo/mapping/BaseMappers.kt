package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.Stats
import de.schnettler.database.models.TopListEntry
import de.schnettler.lastfm.models.BaseAlbumDto
import de.schnettler.lastfm.models.BaseArtistDto
import de.schnettler.lastfm.models.BaseStatsDto
import de.schnettler.lastfm.models.BaseTrackDto

object BaseAlbumMapper : Mapper<BaseAlbumDto, LastFmEntity.Album> {
    override suspend fun map(from: BaseAlbumDto) = LastFmEntity.Album(
        name = from.name,
        url = from.url,
        artist = from.artist,
        imageUrl = from.image.lastOrNull()?.url
    )
}

object BaseArtistMapper : Mapper<BaseArtistDto, LastFmEntity.Artist> {
    override suspend fun map(from: BaseArtistDto) = LastFmEntity.Artist(
        name = from.name,
        url = from.url
    )
}

object BaseTrackMapper : Mapper<BaseTrackDto, LastFmEntity.Track> {
    override suspend fun map(from: BaseTrackDto): LastFmEntity.Track = LastFmEntity.Track(
        name = from.name,
        url = from.url,
        artist = from.artist.name,
    )
}

object BaseStatMapper : Mapper<BaseStatsDto, Stats> {
    override suspend fun map(from: BaseStatsDto) = Stats(
        plays = from.playcount,
        listeners = from.listeners,
        userPlays = from.userplaycount
    )
}

fun createTopListEntry(id: String, type: EntityType, listType: ListType, index: Int, count: Long?) = TopListEntry(
    id = id,
    entityType = type,
    listType = listType,
    index = index,
    count = count ?: 0
)