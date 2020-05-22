package de.schnettler.database

import androidx.room.TypeConverter
import de.schnettler.database.models.ListingType
import de.schnettler.database.models.TopListEntryType

class TypeConverter {

    private val typeMap = ListingType.values().associateBy(ListingType::id)
    private val topListingTypeMap = TopListEntryType.values().associateBy(TopListEntryType::id)

    @TypeConverter
    fun typeEnumToInt(type: ListingType) = type.id

    @TypeConverter
    fun intToTypeEnum(id: Int): ListingType = typeMap[id] ?: ListingType.UNDEFINED

    @TypeConverter
    fun topListingTypeToString(type: TopListEntryType) = type.id

    @TypeConverter
    fun stringToTopListingType(id: String): TopListEntryType = topListingTypeMap[id] ?: TopListEntryType.UNDEFINED

    @TypeConverter
    fun stringToList(value: String) = value.split(",").map { it }

    @TypeConverter
    fun listToString(list: List<String>) = list.joinToString(",")
}