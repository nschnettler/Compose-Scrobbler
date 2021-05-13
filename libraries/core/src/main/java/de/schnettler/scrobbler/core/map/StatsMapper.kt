package de.schnettler.scrobbler.core.map

import de.schnettler.scrobbler.model.Stats
import de.schnettler.scrobbler.model.remote.StatsResponse

object StatsMapper : ParameterMapper<StatsResponse, Stats, String> {
    override suspend fun map(from: StatsResponse, parameter: String) = Stats(
        id = parameter,
        plays = from.playcount,
        listeners = from.listeners,
        userPlays = from.userplaycount
    )
}