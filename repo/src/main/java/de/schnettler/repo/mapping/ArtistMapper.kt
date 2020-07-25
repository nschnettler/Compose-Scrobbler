package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.RelationEntity
import de.schnettler.database.models.Session
import de.schnettler.database.models.TrackDomain
import de.schnettler.database.models.TrackWithAlbum
import de.schnettler.database.models.User
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.lastfm.models.MinimalListing
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.lastfm.models.SpotifyAccessTokenDto
import de.schnettler.lastfm.models.UserArtistDto
import de.schnettler.lastfm.models.UserDto

fun ChartArtistDto.map(): Artist = Artist(
    name = this.name,
    url = this.url,
    plays = this.playcount ?: 0,
    listeners = this.listeners ?: 0
)

fun UserArtistDto.map() = Artist(
    name = this.name,
    url = this.url,
    userPlays = this.playcount ?: 0
)

fun MinimalListing.mapToArtist() = Artist(
    name = this.name,
    url = this.url
)

fun ArtistInfoDto.map() = Artist(
    name = this.name,
    url = this.url,
    plays = this.stats.playcount,
    userPlays = this.stats.userplaycount ?: 0,
    listeners = this.stats.listeners,
    bio = this.bio.content,
    tags = this.tags.tag.map { tag -> tag.name }
)

fun TrackWithAlbum.map() = TrackDomain(
    name = this.track.name,
    artist = this.track.artist,
    userLoved = this.track.userLoved,
    userPlays = this.track.userPlays,
    album = this.album,
    plays = this.track.plays,
    listeners = this.track.listeners,
    duration = this.track.duration,
    url = this.track.url,
    id = this.track.url,
    imageUrl = this.track.imageUrl,
    tags = this.track.tags
)

object SessionMapper : Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session = Session(
        from.name,
        from.key,
        System.currentTimeMillis()
    )
}

object UserMapper : Mapper<UserDto, User> {
    override suspend fun map(from: UserDto): User {
        return User(
            name = from.name,
            playcount = from.playcount,
            url = from.url,
            countryCode = from.country,
            age = from.age,
            realname = from.realname,
            registerDate = from.registerDate.unixtime,
            imageUrl = from.image[3].url
        )
    }
}

object SpotifyAuthMapper : Mapper<SpotifyAccessTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyAccessTokenDto) = AuthToken(
        type = from.token_type,
        token = from.access_token,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}

object RelationMapper : IndexedMapper<Pair<LastFmEntity, LastFmEntity>, RelationEntity> {
    override suspend fun map(index: Int, from: Pair<LastFmEntity, LastFmEntity>): RelationEntity {
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