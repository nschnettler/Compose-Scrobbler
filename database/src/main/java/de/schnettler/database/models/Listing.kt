package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

//open class ListingMin(
//    @PrimaryKey open val name: String,
//    open val plays: Long = 0,
//    open val listeners: Long = 0,
//    open val imageUrl: String? = null
//) {
//    val type = when(this) {
//        is Album -> ListingType.ALBUM
//        is Artist -> ListingType.ARTIST
//        is Track -> ListingType.TRACK
//        else -> ListingType.UNDEFINED
//    }
//}

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

data class MinimalEntity(
    override val id: String,
    override val name: String,
    override val plays: Long,
    override val userPlays: Long,
    override val listeners: Long,
    override val url: String?,
    override var imageUrl: String?

): ListingMin

//open class Listing(
//    override val name: String,
//    open val url: String,
//    override val plays: Long,
//    override val listeners: Long = 0,
//    override val imageUrl: String? = null
//): IListingMin