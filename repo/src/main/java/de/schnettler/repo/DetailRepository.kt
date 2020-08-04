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
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.RelatedArtistEntry
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.AlbumInfoMapper
import de.schnettler.repo.mapping.AlbumWithStatsMapper
import de.schnettler.repo.mapping.ArtistInfoMapper
import de.schnettler.repo.mapping.ArtistTrackMapper
import de.schnettler.repo.mapping.EntityMapper
import de.schnettler.repo.mapping.TrackMapper
import de.schnettler.repo.mapping.forLists
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val statsDao: StatsDao,
    private val entityInfoDao: EntityInfoDao,
    private val relationDao: ArtistRelationDao,
    private val albumInfoMapper: AlbumInfoMapper,
    private val service: LastFmService,
    private val artistInfoMapper: ArtistInfoMapper,
    private val artistTrackMapper: ArtistTrackMapper,
    private val entityMapper: EntityMapper,
    private val albumWithStatsMapper: AlbumWithStatsMapper,
    private val lastFmAuthProvider: LastFmAuthProvider,
    private val trackMapper: TrackMapper
) {
    val artistStore = StoreBuilder.from(
        fetcher = Fetcher.of { artist: Artist ->
            val response =
                service.getArtistInfo(artist.name, lastFmAuthProvider.getSessionKeyOrThrow())
            artistInfoMapper.map(response).apply {
                topAlbums = albumWithStatsMapper.forLists()(service.getArtistAlbums(artist.name))
                topTracks = artistTrackMapper.forLists()(service.getArtistTracks(artist.name))
                similarArtists = entityMapper.forLists()(response.similar.artist)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { artist: Artist ->
                val artistStatsInfo = artistDao.getArtistWithMetadata(artist.id)
                val topTracks = trackDao.getTopTracksOfArtist(artist.name)
                val topAlbums = albumDao.getTopAlbumsOfArtist(artist.name)
                val similarArtists = artistDao.getRelatedArtists(artist.id)
//                val info = entityInfoDao.getEntityInfo(artist.id)
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
                relationDao.forceInsertAll(details.similarArtists.mapIndexed { index, related ->
                    RelatedArtistEntry(artistId = details.info.id, related.id, index)
                })
            }
        )
    ).build()

    val trackStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: Track ->
            trackMapper.map(service.getTrackInfo(key.artist, key.name, lastFmAuthProvider.getSessionKeyOrThrow()))
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key -> trackDao.getTrackWithMetadata(key.id, key.artist) },
            writer = { _: Track, (track, stats, info) ->
                trackDao.insertTrackOrUpdateAlbum(track) // Album property is loaded now
                entityInfoDao.insert(info)
                statsDao.insertOrUpdateStats(listOf(stats))
            }
        )
    ).build()

    val albumStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: Album ->
            val albumInfo = service.getAlbumInfo(
                artistName = key.artist,
                albumName = key.name, sessionKey = lastFmAuthProvider.getSessionKeyOrThrow()
            )
            albumInfoMapper.map(albumInfo)
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
                trackDao.forceInsertAll(albumDetails.tracks.map { it.entity }) // Have Images now
                entityInfoDao.insertAll(albumDetails.tracks.map { it.info })
            }
        )
    ).build()
}