package de.schnettler.database.models

interface CommonEntity {
    val id: String
    val name: String
    val type: ListingType
        get() = when(this) {
            is Album -> ListingType.ALBUM
            is Artist -> ListingType.ARTIST
            is Track -> ListingType.TRACK
            else -> ListingType.UNDEFINED
        }
}

interface LastFmEntity: CommonEntity {
    val url: String
    var imageUrl: String?
}

interface LastFmStatsEntity: LastFmEntity {
    val plays: Long
    val userPlays: Long
    val listeners: Long
}