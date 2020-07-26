package de.schnettler.database

import androidx.room.TypeConverter
import de.schnettler.database.models.ListingType
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.database.models.TopListEntryType

class TypeConverter {

    private val typeMap = ListingType.values().associateBy(ListingType::id)
    private val topListingTypeMap = TopListEntryType.values().associateBy(TopListEntryType::id)
    private val scrobbleStatusTypeMap = ScrobbleStatus.values().associateBy(ScrobbleStatus::name)

    @TypeConverter
    fun typeEnumToInt(type: ListingType) = type.id

    @TypeConverter
    fun intToTypeEnum(id: Int): ListingType = typeMap[id] ?: ListingType.UNDEFINED

    @TypeConverter
    fun topListingTypeToString(type: TopListEntryType) = type.id

    @TypeConverter
    fun stringToTopListingType(id: String): TopListEntryType =
        topListingTypeMap[id] ?: TopListEntryType.UNDEFINED

    @TypeConverter
    fun stringToList(value: String) = value.split(",").map { it }.filter { it.isNotBlank() }

    @TypeConverter
    fun listToString(list: List<String>) = list.joinToString(",")

    @TypeConverter
    fun scrobbleStatusToString(status: ScrobbleStatus) = status.name

    @TypeConverter
    fun stringToScrobbleStatus(name: String) =
        scrobbleStatusTypeMap[name] ?: ScrobbleStatus.VOLATILE
}