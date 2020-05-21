package de.schnettler.database

import androidx.room.TypeConverter
import de.schnettler.database.models.ListingType

class TypeConverter {

    private val typeMap = ListingType.values().associateBy(ListingType::id)

    @TypeConverter
    fun typeEnumToInt(type: ListingType) = type.id

    @TypeConverter
    fun intToTypeEnum(id: Int): ListingType = typeMap[id] ?: ListingType.UNDEFINED

    @TypeConverter
    fun stringToList(value: String) = value.split(",").map { it }

    @TypeConverter
    fun listToString(list: List<String>) = list.joinToString(",")
}