package de.schnettler.scrobbler.details.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.details.api.AlbumApi
import de.schnettler.scrobbler.details.db.AlbumDetailDao
import de.schnettler.scrobbler.details.db.EntityInfoDao
import de.schnettler.scrobbler.details.db.StatsDao
import de.schnettler.scrobbler.details.map.AlbumInfoMapper
import de.schnettler.scrobbler.model.LastFmEntity.Album
import de.schnettler.scrobbler.persistence.dao.TrackDao
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AlbumDetailRepository @Inject constructor(
    private val detailDao: AlbumDetailDao,
    private val trackDao: TrackDao,
    private val statsDao: StatsDao,
    private val entityInfoDao: EntityInfoDao,
    private val albumApi: AlbumApi,
) {
    val albumStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: Album ->
            AlbumInfoMapper.map(
                albumApi.getAlbumInfo(name = key.artist, albumName = key.name)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key ->
                val details = detailDao.getAlbumWithStatsAndInfo(id = key.id, artist = key.artist)
                val tracks = detailDao.getTracksFromAlbum(key.artist, key.name)
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
                detailDao.forceInsert(album)
                statsDao.insertOrUpdateStats(listOf(stats))
                entityInfoDao.insert(info)
                trackDao.forceInsertAll(albumDetails.tracks.map { it.entity }) // Update album, overrides albumid
                entityInfoDao.insertAll(albumDetails.tracks.map { it.info })
            }
        )
    ).build()
}