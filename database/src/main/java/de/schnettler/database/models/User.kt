package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val name: String,
    val playcount: Long,
    val url: String,
    val age: Long,
    val realname: String,
    val registerDate: Long,
    val countryCode: String,
    val imageUrl: String
)