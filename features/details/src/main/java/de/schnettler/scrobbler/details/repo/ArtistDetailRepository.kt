package de.schnettler.scrobbler.details.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.details.api.ArtistApi
import de.schnettler.scrobbler.details.db.ArtistDetailDao
import de.schnettler.scrobbler.details.db.ArtistRelationDao
import de.schnettler.scrobbler.details.db.EntityInfoDao
import de.schnettler.scrobbler.details.db.StatsDao
import de.schnettler.scrobbler.details.map.ArtistAlbumResponseMapper
import de.schnettler.scrobbler.details.map.ArtistInfoMapper
import de.schnettler.scrobbler.details.map.ArtistTrackMapper
import de.schnettler.scrobbler.details.model.ArtistDetailEntity
import de.schnettler.scrobbler.details.model.RelatedArtistEntry
import de.schnettler.scrobbler.image.repo.ImageRepo
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ArtistDetailRepository @Inject constructor(
    private val detailDao: ArtistDetailDao,
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val statsDao: StatsDao,
    private val relationDao: ArtistRelationDao,
    private val entityInfoDao: EntityInfoDao,
    private val artistApi: ArtistApi,
    private val imageRepo: ImageRepo,
) {
    val artistStore = StoreBuilder.from(
        fetcher = Fetcher.of { artist: LastFmEntity.Artist ->
            // Refresh Image
            imageRepo.updateArtistImage(artist)

            coroutineScope {
                val infoResult = async {
                    artistApi.getArtistInfo(artist.name)
                }
                val albums = async { artistApi.getArtistAlbums(artist.name) }
                val tracks = async { artistApi.getArtistTracks(artist.name) }

                val info = infoResult.await()
                ArtistInfoMapper.map(info).apply {
                    topAlbums = ArtistAlbumResponseMapper.forLists()(albums.await())
                    topTracks = ArtistTrackMapper.forLists()(tracks.await())
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { artist: LastFmEntity.Artist ->
                val artistStatsInfo = detailDao.getArtistDetails(artist.id)
                val topTracks = detailDao.getTopTracksOfArtist(artist.name)
                val topAlbums = detailDao.getTopAlbumsOfArtist(artist.name)
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
            writer = { _: LastFmEntity.Artist, details: ArtistDetailEntity ->
                detailDao.insert(details.artist)
                entityInfoDao.insert(details.info)
                statsDao.insertOrUpdateStats(listOf(details.stats))

                trackDao.insertAll(details.topTracks.map { it.entity })
                statsDao.insertOrUpdateStats(details.topTracks.map { it.stats })

                albumDao.forceInsertAll(details.topAlbums.map { it.entity })
                statsDao.insertOrUpdateStats(details.topAlbums.map { it.stats })

                detailDao.insertAll(details.similarArtists)
                details.info?.let { info ->
                    relationDao.forceInsertAll(details.similarArtists.mapIndexed { index, related ->
                        RelatedArtistEntry(artistId = info.id, related.id, index)
                    })
                }
            }
        )
    ).build()
}