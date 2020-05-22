package de.schnettler.repo.mapping

import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListEntryType

object TopListMapper: IndexedMapper<Pair<ListingMin, TopListEntryType>, TopListEntry> {
    override suspend fun map(index: Int, from: Pair<ListingMin, TopListEntryType>) = TopListEntry(
        id = from.first.id,
        type = from.second,
        index = index
    )
}
