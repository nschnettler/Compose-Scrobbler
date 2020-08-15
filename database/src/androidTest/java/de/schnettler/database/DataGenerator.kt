package de.schnettler.database

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.Stats
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListTrack

object DataGenerator {
    fun generateAlbums(number: Int, artistName: String? = null) = List(number) {
        generateAlbum(it, artistName)
    }

    fun generateAlbum(current: Int = 0, artist: String?) =
        LastFmEntity.Album("album$current", "url$current", artist ?: "artist$current")

    fun generateAlbumsWithStats(count: Int, artist: String?) = List(count) {
        val album = generateAlbum(it, artist)
        EntityWithStats.AlbumWithStats(
            album,
            generateStat(album.id, it)
        )
    }

    fun generateAlbumWithStatsAndInfo(count: Int, artist: String?) = List(count) {
        val album = generateAlbum(it, artist)
        EntityWithStatsAndInfo.AlbumWithStatsAndInfo(
            album,
            generateStat(album.id, it),
            generateInfo(album.id)
        )
    }

    fun generateArtistWithStatsAndInfo(count: Int) = List(count) {
        val artist = generateArtist(it)
        EntityWithStatsAndInfo.ArtistWithStatsAndInfo(
            artist,
            generateStat(artist.id, it),
            generateInfo(artist.id)
        )
    }

    fun generateArtistWithTopListEntry(count: Int, listType: ListType) = List(count) {
        val artist = generateArtist(it)
        TopListArtist(
            TopListEntry(artist.id, EntityType.ARTIST, listType, it, 0),
            artist
        )
    }

    fun generateTracksWithTopListEntry(count: Int, listType: ListType) = List(count) {
        val track = generateTrack(it)
        TopListTrack(
            TopListEntry(id = track.id, entityType = EntityType.TRACK, listType = listType, index = it, count = 0),
            track
        )
    }

    fun generateAlbumWithTopListEntry(count: Int, listType: ListType, artist: String = "artist") = List(count) {
        val album = generateAlbum(it, artist)
        TopListAlbum(
            TopListEntry(id = album.id, entityType = EntityType.ALBUM, listType = listType, index = it, count = 0),
            album
        )
    }

    fun generateStats(count: Int, prefix: String = "stat") = List(count) {
        generateStat("$prefix$it", it)
    }

    private fun generateStat(forId: String, number: Int) = Stats(forId, 10L * number, 20L * number, 30L * number)

    private fun generateInfo(forId: String) = EntityInfo(forId, emptyList(), 10, "wiki")

    fun generateArtists(number: Int, prefix: String = "artist") = List(number) {
        generateArtist(it, prefix)
    }

    fun generateTracks(number: Int, prefix: String = "track", artist: String = "artist") = List(number) {
        generateTrack(it, prefix, artist)
    }

    private fun generateArtist(current: Int = 0, prefix: String = "artist") =
        LastFmEntity.Artist("$prefix$current", "url$current")

    private fun generateTrack(current: Int = 0, prefix: String = "track", artist: String = "artist") =
        LastFmEntity.Track("$prefix$current", "url$current", artist)
}
