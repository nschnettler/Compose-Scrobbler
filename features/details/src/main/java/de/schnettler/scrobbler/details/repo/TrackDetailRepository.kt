package de.schnettler.scrobbler.details.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.details.api.TrackApi
import de.schnettler.scrobbler.details.api.TrackPostApi
import de.schnettler.scrobbler.details.db.AlbumDetailDao
import de.schnettler.scrobbler.details.db.EntityInfoDao
import de.schnettler.scrobbler.details.db.StatsDao
import de.schnettler.scrobbler.details.db.TrackDetailDao
import de.schnettler.scrobbler.details.map.TrackInfoMapper
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.LastFmEntity
import javax.inject.Inject

class TrackDetailRepository @Inject constructor(
    private val trackDao: TrackDetailDao,
    private val albumDao: AlbumDetailDao,
    private val statsDao: StatsDao,
    private val entityInfoDao: EntityInfoDao,
    private val trackApi: TrackApi,
    private val trackPostApi: TrackPostApi,
) {
    val trackStore = StoreBuilder.from(
        fetcher = Fetcher.of { key: LastFmEntity.Track ->
            TrackInfoMapper.map(trackApi.getTrackInfo(key.artist, key.name))
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key -> trackDao.getTrackWithMetadata(key.id, key.artist) },
            writer = { _: LastFmEntity.Track, (track, stats, info, album) ->
                album?.let { albumDao.insert(album) }
                trackDao.inserTrackOrUpdateMetadata(track) // Update Album, AlbumId, ImageUrl
                entityInfoDao.forceInsert(info)
                statsDao.insertOrUpdateStats(listOf(stats))
            }
        )
    ).build()

    suspend fun toggleTrackLikeStatus(track: LastFmEntity.Track, info: EntityInfo) {
        val result = if (info.loved) {
            trackPostApi.loveTrack(track = track.name, artist = track.artist)
        } else {
            trackPostApi.unloveTrack(track = track.name, artist = track.artist)
        }
        if (result.isSuccessful) {
            entityInfoDao.update(info)
        }
    }
}