package de.schnettler.scrobbler.authentication.map

import de.schnettler.scrobbler.authentication.model.Session
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.scrobbler.core.map.Mapper

object SessionMapper : Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session =
        Session(
            from.name,
            from.key,
            System.currentTimeMillis()
        )
}