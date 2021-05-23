package de.schnettler.scrobbler.persistence

import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun stringToList(value: String) = value.split(",").map { it }.filter { it.isNotBlank() }

    @TypeConverter
    fun listToString(list: List<String>) = list.joinToString(",")
}