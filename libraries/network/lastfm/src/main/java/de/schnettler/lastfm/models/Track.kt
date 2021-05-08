package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.TrackResponse

@JsonClass(generateAdapter = true)
open class BaseTrackDto(
    open val name: String,
    open val url: String,
    open val artist: MinimalArtist,
    open val album: TrackAlbum?
)



@JsonClass(generateAdapter = true)
data class AlbumTrack(
    override val name: String,
    override val url: String,
    override val artist: MinimalArtist,

    val duration: Long,

    val mbid: String?,
) : TrackResponse

@JsonClass(generateAdapter = true)
data class ArtistTracksDto(
    override val name: String,
    override val url: String,
    override val artist: MinimalArtist,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    val mbid: String?,
) : BaseTrackDto(name, url, artist, null), BaseStatsDto