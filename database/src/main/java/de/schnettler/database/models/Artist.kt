package de.schnettler.database.models

data class Artist(
    val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String,
    val images: ImageUrls
)

data class ImageUrls(
    val s: String,
    val m: String,
    val l: String,
    val xl: String,
    val xxl: String
)