package de.schnettler.repo.mapping.user

import de.schnettler.database.models.User
import de.schnettler.lastfm.models.UserDto
import de.schnettler.repo.mapping.Mapper

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