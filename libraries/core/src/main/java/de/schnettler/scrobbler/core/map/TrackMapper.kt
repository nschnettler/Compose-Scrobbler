package de.schnettler.scrobbler.core.map

import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.remote.TrackResponse

object TrackMapper : ParameterMapper<TrackResponse, LastFmEntity.Track, LastFmEntity.Album?> {
    override suspend fun map(from: TrackResponse, album: LastFmEntity.Album?): LastFmEntity.Track =
        LastFmEntity.Track(
            name = from.name,
            url = from.url,
            artist = from.artist.name,
            album = album?.name,
            albumId = album?.id,
            imageUrl = album?.imageUrl
        )
}