package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.common.TimePeriod
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TopListDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.UserAlbumMapper
import de.schnettler.repo.mapping.UserArtistMapper
import de.schnettler.repo.mapping.UserTrackMapper
import de.schnettler.repo.mapping.forLists
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val chartDao: ChartDao,
    private val topListDao: TopListDao,
    private val service: LastFmService,
    private val albumMapper: UserAlbumMapper,
    private val artistMapper: UserArtistMapper,
    private val trackMapper: UserTrackMapper,
    private val authProvider: LastFmAuthProvider
) {
    val topArtistStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val session = authProvider.getSessionOrThrow()
            val response = service.getUserTopArtists(timePeriod, session.key)
            if (timePeriod == TimePeriod.OVERALL) {
                userDao.updateArtistCount(session.name, response.info.total)
            }
            artistMapper.forLists()(response.artist)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopArtists(listType = ListType.USER) },
            writer = { _: Any, entries: List<TopListArtist> ->
                artistDao.insertAll(entries.map { it.value })
                topListDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    val topAlbumStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            albumMapper.forLists()(
                service.getUserTopAlbums(timePeriod, authProvider.getSessionKeyOrThrow())
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopAlbums() },
            writer = { _: Any, entries: List<TopListAlbum> ->
                albumDao.forceInsertAll(entries.map { it.value })
                topListDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    val topTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            trackMapper.forLists()(
                service.getUserTopTracks(timePeriod, authProvider.getSessionKeyOrThrow())
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopTracks() },
            writer = { _: Any, entries: List<TopListTrack> ->
                trackDao.insertAll(entries.map { it.value })
                topListDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()
}