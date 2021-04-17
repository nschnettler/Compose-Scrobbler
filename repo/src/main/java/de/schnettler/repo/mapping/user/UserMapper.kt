package de.schnettler.repo.mapping.user

import de.schnettler.scrobbler.model.User
import de.schnettler.lastfm.models.UserDto
import de.schnettler.scrobbler.core.map.Mapper

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