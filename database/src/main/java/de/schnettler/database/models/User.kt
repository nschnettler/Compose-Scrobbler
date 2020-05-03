package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val name: String,
    val playcount: Long,
    val url: String,
    val country: String,
    val age: Long,
    val realname: String,
    val registerDate: Long
)