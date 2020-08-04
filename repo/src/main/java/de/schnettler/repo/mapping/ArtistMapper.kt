package de.schnettler.repo.mapping

import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType
import de.schnettler.database.models.Session
import de.schnettler.database.models.User
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.lastfm.models.SpotifyTokenDto
import de.schnettler.lastfm.models.UserDto

object SessionMapper : Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session = Session(
        from.name,
        from.key,
        System.currentTimeMillis()
    )
}

object UserMapper : Mapper<UserDto, User> {
    override suspend fun map(from: UserDto) = User(
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

object SpotifyAuthMapper : Mapper<SpotifyTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyTokenDto) = AuthToken(
        type = from.type,
        token = from.accessToken,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}