package de.schnettler.scrobbler.profile.map

import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.model.User
import de.schnettler.scrobbler.profile.model.remote.UserInfoResponse

object UserMapper : Mapper<UserInfoResponse, User> {
    override suspend fun map(from: UserInfoResponse) = User(
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