package de.schnettler.scrobbler.authentication.map

import de.schnettler.scrobbler.authentication.model.Session
import de.schnettler.scrobbler.authentication.model.SessionResponse
import de.schnettler.scrobbler.core.map.Mapper

object SessionMapper : Mapper<SessionResponse, Session> {
    override suspend fun map(from: SessionResponse): Session =
        Session(
            from.name,
            from.key,
            System.currentTimeMillis()
        )
}