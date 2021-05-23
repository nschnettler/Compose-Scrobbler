package de.schnettler.repo.mapping.auth

import de.schnettler.database.models.Session
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.repo.mapping.Mapper

object SessionMapper : Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session = Session(
        from.name,
        from.key,
        System.currentTimeMillis()
    )
}