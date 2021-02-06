package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ArtistRelationDao
import de.schnettler.database.daos.EntityInfoDao
import de.schnettler.database.daos.StatsDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.RelatedArtistEntry
import de.schnettler.lastfm.api.lastfm.ArtistService
import de.schnettler.lastfm.api.lastfm.DetailService
import de.schnettler.lastfm.api.lastfm.PostService
import de.schnettler.lastfm.api.lastfm.PostService.Companion.METHOD_LOVE
import de.schnettler.lastfm.api.lastfm.PostService.Companion.METHOD_UNLOVE
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.album.AlbumInfoMapper
import de.schnettler.repo.mapping.album.AlbumWithStatsMapper
import de.schnettler.repo.mapping.artist.ArtistInfoMapper
import de.schnettler.repo.mapping.artist.ArtistTrackMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.track.TrackInfoMapper
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val statsDao: StatsDao,
    private val entityInfoDao: EntityInfoDao,
    private val relationDao: ArtistRelationDao,
    private val detailService: DetailService,
    private val artistService: ArtistService,
    private val imageRepo: ImageRepo,
    private val authProvider: LastFmAuthProvider,
    private val postService: PostService,
) {
    val artistStore = StoreBuilder.from(
        fetcher = Fetcher.of { artist: Artist ->
            // Refresh Image
            imageRepo.updateArtistImage(artist)

            coroutineScope {
                val infoResult = async {
                    detailService.getArtistInfo(artist.name)
                }
                val albums = async { artistService.getArtistAlbums(artist.name) }
                val tracks = async { artistService.getArtistTracks(artist.name) }

                val info = infoResult.await()
                ArtistInfoMapper.map(info).apply {
                    topAlbums = AlbumWithStatsMapper.forLists()(albums.await())
                    topTracks = ArtistTrackMapper.forLists()(tracks.await())
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { artist: Artist ->
                val artistStatsInfo = artistDao.getArtistWithMetadata(artist.id)
                val topTracks = trackDao.getTopTracksOfArtist(artist.name)
                val topAlbums = albumDao.getTopAlbumsOfArtist(artist.name)
                val similarArtists = relationDao.getRelatedArtists(artist.id)
                combine(
                    artistStatsInfo,
                    topTracks,
                    topAlbums,
                    similarArtists
                ) { artistDetails, tracks, albums, similar ->
                    artistDetails.apply {
                        this?.topAlbums = albums
                        this?.topTracks = tracks
                        this?.similarArtists = similar.map { it.artist }
                    }
                }
            },
            writer = { _: Artist, details: ArtistWithStatsAndInfo ->
                artistDao.insert(details.entity)
                entityInfoDao.insert(details.info)
                statsDao.insertOrUpdateStats(listOf(details.stats))

                trackDao.insertAll(details.topTracks.map { it.entity })
                statsDao.insertOrUpdateStats(details.topTracks.map { it.stats })

                albumDao.forceInsertAll(details.topAlbums.map { it.entity })
                statsDao.insertOrUpdateStats(details.topAlbums.map { it.stats })

                artistDao.insertAll(details.similarArtists)
                details.info?.let { info ->
                    relationDao.forceInsertAll(details.similarArtists.mapIndexed { index, related ->
                        RelatedArtistEntry(artistId = info.id, related.id, index)
                    })
                }
            }
        )
    ).build()

    val trackStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: Track ->
            TrackInfoMapper.map(
                detailService.getTrackInfo(key.artist, key.name)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key -> trackDao.getTrackWithMetadata(key.id, key.artist) },
            writer = { _: Track, (track, stats, info, album) ->
                album?.let { albumDao.insert(album) }
                trackDao.inserTrackOrUpdateMetadata(track) // Update Album, AlbumId, ImageUrl
                entityInfoDao.forceInsert(info)
                statsDao.insertOrUpdateStats(listOf(stats))
            }
        )
    ).build()

    val albumStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: Album ->
            AlbumInfoMapper.map(
                detailService.getAlbumInfo(name = key.artist, albumName = key.name)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key ->
                val details = albumDao.getAlbumWithStatsAndInfo(id = key.id, artist = key.artist)
                val tracks = trackDao.getTracksFromAlbum(key.artist, key.name)
                val result = combine(details, tracks) { albumDetails, albumTracks ->
                    albumDetails.apply {
                        this?.tracks = albumTracks
                    }
                    albumDetails
                }
                result
            },
            writer = { _, albumDetails ->
                val (album, stats, info) = albumDetails
                albumDao.forceInsert(album)
                statsDao.insertOrUpdateStats(listOf(stats))
                entityInfoDao.insert(info)
                trackDao.forceInsertAll(albumDetails.tracks.map { it.entity }) // Update album, overrides albumid
                entityInfoDao.insertAll(albumDetails.tracks.map { it.info })
            }
        )
    ).build()

    suspend fun toggleTrackLikeStatus(track: Track, info: EntityInfo) {
        val method = if (info.loved) METHOD_LOVE else METHOD_UNLOVE
        val sig = createSignature(
            mutableMapOf(
                "method" to method,
                "track" to track.name,
                "artist" to track.artist,
                "sk" to authProvider.getSessionKey()
            )
        )
        val result = postService.toggleTrackLoveStatus(
            method = method,
            track = track.name,
            artist = track.artist,
            sessionKey = authProvider.getSessionKey(),
            signature = sig
        )
        if (result.isSuccessful) {
            entityInfoDao.update(info)
        }
    }
}