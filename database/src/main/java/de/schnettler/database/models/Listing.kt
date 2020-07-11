package de.schnettler.database.models

interface ListingMin {
    val id: String
    val name: String
    val plays: Long
    val userPlays: Long
    val listeners: Long
    val url: String?
    var imageUrl: String?
    val type: ListingType
        get() = when(this) {
            is Album -> ListingType.ALBUM
            is Artist -> ListingType.ARTIST
            is Track -> ListingType.TRACK
            else -> ListingType.UNDEFINED
        }
}