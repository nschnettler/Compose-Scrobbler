package de.schnettler.scrobbler.profile.repo

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.image.repo.GET_ARTIST_IMAGES_WORK
import de.schnettler.scrobbler.image.repo.SpotifyWorker
import de.schnettler.scrobbler.model.TimePeriod
import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import de.schnettler.scrobbler.profile.api.ProfileApi
import de.schnettler.scrobbler.profile.db.ToplistDao
import de.schnettler.scrobbler.profile.map.TopUserAlbumMapper
import de.schnettler.scrobbler.profile.map.TopUserArtistMapper
import de.schnettler.scrobbler.profile.map.TopUserTrackMapper
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: de.schnettler.scrobbler.profile.db.UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val toplistDao: ToplistDao,
    private val profileApi: ProfileApi,
    private val workManager: WorkManager,
) {
    val topArtistStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val response = profileApi.getTopArtists(timePeriod)
            if (timePeriod == TimePeriod.OVERALL) {
                userDao.updateArtistCount(response.info.total)
            }
            TopUserArtistMapper.forLists()(response.artist)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { toplistDao.getTopArtists() },
            writer = { _: Any, entries: List<TopListArtist> ->
                artistDao.insertAll(entries.map { it.value })
                toplistDao.forceInsertAll(entries.map { it.listing })
                startSpotifyImageWorker()
            }
        )
    ).build()

    val topAlbumStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            TopUserAlbumMapper.forLists()(
                profileApi.getTopAlbums(timePeriod)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { toplistDao.getTopAlbums() },
            writer = { _: Any, entries: List<TopListAlbum> ->
                albumDao.forceInsertAll(entries.map { it.value })
                toplistDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    val topTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            TopUserTrackMapper.forLists()(
                profileApi.getTopTracks(timePeriod)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { toplistDao.getTopTracks() },
            writer = { _: Any, entries: List<TopListTrack> ->
                trackDao.insertAll(entries.map { it.value })
                toplistDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    private fun startSpotifyImageWorker() {
        val request = OneTimeWorkRequestBuilder<SpotifyWorker>()
            .build()
        workManager.enqueueUniqueWork(
            GET_ARTIST_IMAGES_WORK,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}