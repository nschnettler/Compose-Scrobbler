package de.schnettler.repo.mapping

import de.schnettler.database.models.*
import de.schnettler.lastfm.models.*


object ArtistMinMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto): Artist = Artist(
        name = from.name,
        plays = from.playcount ?: 0,
        listeners = from.listeners ?: 0,
        url = from.url
    )
}

object ArtistMapper: Mapper<ArtistInfoDto, Artist> {
    override suspend fun map(from: ArtistInfoDto) = Artist(
        name = from.name,
        url = from.url,
        plays = from.stats.playcount,
        listeners = from.stats.listeners,
        bio = from.bio.content,
        userplays = from.stats.userplaycount?: 0,
        tags = from.tags.tag.map { tag -> tag.name }
    )
}

object AlbumMapper : Mapper<AlbumDto, Album> {
    override suspend fun map(from: AlbumDto): Album = Album(
        name = from.name,
        artist = from.artist.name,
        plays = from.playcount,
        url = from.url,
        imageUrl = from.images[3].url
    )
}

object SessionMapper: Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session = Session(
        from.name,
        from.key,
        System.currentTimeMillis()
    )
}

object UserMapper: Mapper<UserDto, User> {
    override suspend fun map(from: UserDto): User {
        val user = User(
            name = from.name,
            playcount = from.playcount,
            url = from.url,
            countryCode = from.country,
            age = from.age,
            realname = from.realname,
            registerDate = from.registerDate.unixtime,
            imageUrl = from.image[3].url
        )
        return user
    }
}

object TrackMapper: Mapper<TrackDto, Track> {
    override suspend fun map(from: TrackDto) = Track(
        trackId = from.mbid,
        name = from.name,
        url = from.url,
        plays = from.playcount,
        artist = from.artist.name,
        listeners = from.listeners ?: 0
    )
}

object TrackWithAlbumMapper: Mapper<TrackWithAlbumDto, Track> {
    override suspend fun map(from: TrackWithAlbumDto) = Track(
        name = from.name,
        trackId = from.mbid,
        album = from.album.name,
        artist = from.artist.name,
        url = from.url
    )
}

object SpotifyAuthMapper: Mapper<SpotifyAccessTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyAccessTokenDto) = AuthToken(
        type = from.token_type,
        token = from.access_token,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}

object RelationMapper: IndexedMapper<Pair<ListingMin, ListingMin>, RelationEntity> {
    override suspend fun map(index: Int, from: Pair<ListingMin, ListingMin>): RelationEntity {
        val source = from.first
        val target = from.second
        return RelationEntity(
            sourceId = source.id,
            sourceType = source.type,
            targetId = target.id,
            targetType = target.type,
            index = index
        )
    }
}