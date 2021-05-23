package de.schnettler.scrobbler.core.map

import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListEntry

fun createTopListEntry(id: String, type: EntityType, listType: ListType, index: Int, count: Long?) = TopListEntry(
    id = id,
    entityType = type,
    listType = listType,
    index = index,
    count = count ?: 0
)