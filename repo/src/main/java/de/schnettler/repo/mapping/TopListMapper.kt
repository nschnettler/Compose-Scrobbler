package de.schnettler.repo.mapping

import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListEntryType

object TopListMapper: IndexedMapper<Pair<LastFmStatsEntity, TopListEntryType>, TopListEntry> {
    override suspend fun map(index: Int, from: Pair<LastFmStatsEntity, TopListEntryType>) = TopListEntry(
        id = from.first.id,
        type = from.second,
        index = index,
        count = from.first.userPlays
    )
}
