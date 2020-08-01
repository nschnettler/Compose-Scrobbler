package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.UserArtistDto
import de.schnettler.lastfm.models.UserTrackDto
import javax.inject.Inject

class UserArtistMapper @Inject constructor() : IndexedMapper<UserArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: UserArtistDto): TopListArtist {
        val artist = Artist(
            name = from.name,
            url = from.url,
        )
        val toplist = TopListEntry(
            id = artist.id,
            type = EntityType.ARTIST,
            index = index,
            count = from.playcount ?: 0
        )
        return TopListArtist(toplist, artist)
    }
}

class UserAlbumMapper @Inject constructor() : IndexedMapper<AlbumDto, TopListAlbum> {
    override suspend fun map(index: Int, from: AlbumDto): TopListAlbum {
        val album = Album(
            name = from.name,
            url = from.url,
            artist = from.artist.name
        )
        val toplist = TopListEntry(
            id = album.id,
            type = EntityType.ALBUM,
            index = index,
            count = from.playcount
        )
        return TopListAlbum(toplist, album)
    }
}

class UserTrackMapper @Inject constructor() : IndexedMapper<UserTrackDto, TopListTrack> {
    override suspend fun map(index: Int, from: UserTrackDto): TopListTrack {
        val track = Track(
            name = from.name,
            url = from.url,
            artist = from.artist.name
        )
        val toplist = TopListEntry(
            id = track.id,
            type = EntityType.TRACK,
            index = index,
            count = from.playcount
        )
        return TopListTrack(toplist, track)
    }
}