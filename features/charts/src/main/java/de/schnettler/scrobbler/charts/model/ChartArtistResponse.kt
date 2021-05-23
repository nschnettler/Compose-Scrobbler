package de.schnettler.scrobbler.charts.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ArtistResponse

@JsonClass(generateAdapter = true)
data class ChartArtistResponse(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?,
    val listeners: Long?
) : ArtistResponse