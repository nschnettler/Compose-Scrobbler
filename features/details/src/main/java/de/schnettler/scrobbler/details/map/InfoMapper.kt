package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.ktx.toBoolean
import de.schnettler.scrobbler.core.map.ParameterMapper
import de.schnettler.scrobbler.details.model.InfoResponse
import de.schnettler.scrobbler.model.EntityInfo

object InfoMapper : ParameterMapper<InfoResponse, EntityInfo, String> {
    override suspend fun map(from: InfoResponse, id: String) = EntityInfo(
        id = id,
        tags = from.tags?.tag?.map { tag -> tag.name } ?: emptyList(),
        durationInSeconds = from.duration,
        wiki = from.wiki?.summary,
        loved = from.userloved.toBoolean()
    )
}