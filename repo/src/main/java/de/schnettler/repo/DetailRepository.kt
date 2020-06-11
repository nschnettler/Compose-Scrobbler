package de.schnettler.repo

import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.RelationshipDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.RelationMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.map
import de.schnettler.repo.mapping.mapToArtist
import de.schnettler.repo.util.provideSpotifyService
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val relationDao: RelationshipDao,
    private val service: LastFmService,
    private val lastFmAuthProvider: LastFmAuthProvider,
    private val spotifyAuthProvider: SpotifyAuthProvider,
    private val spotifyAuthenticator: AccessTokenAuthenticator
) {
    fun getArtistInfo(id: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: String ->
            val response = service.getArtistInfo(key, lastFmAuthProvider.session!!.key)
            val artist = response.map()
            refreshImageUrl(artistDao.getArtistImageUrl(key), artist)
            artist.topAlbums = service.getArtistAlbums(key).map { it.map() }
            artist.topTracks = service.getArtistTracks(key).map { it.map() }
            artist.similarArtists = response.similar.artist.map { it.mapToArtist() }
            artist
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { key: String ->
                var artist = artistDao.getArtist(key)
                artist = artist.combine(
                    relationDao.getRelatedAlbums(
                        key,
                        ListingType.ARTIST
                    )
                ) { artist, albums ->
                    artist?.topAlbums = albums.map { it.album }
                    return@combine artist
                }.combine(relationDao.getRelatedTracks(key, ListingType.ARTIST)) { artist, tracks ->
                    artist?.topTracks = tracks.map { it.track }
                    return@combine artist
                }.combine(
                    relationDao.getRelatedArtists(
                        key,
                        ListingType.ARTIST
                    )
                ) { artist, artists ->
                    artist?.similarArtists = artists.map { it.artist }
                    return@combine artist
                }
                artist
            },
            writer = { key: String, artist: Artist ->
                //Make sure to not overwrite the already saved ImageUrl
                val oldImageUrl = artistDao.getArtistImageUrl(key)
                oldImageUrl?.let {
                    artist.imageUrl = oldImageUrl
                }
                artistDao.forceInsert(artist)
                //Tracks
                trackDao.insertEntriesWithRelations(
                    artist.topTracks,
                    RelationMapper.forLists().invoke(artist.topTracks.map { Pair(artist, it) })
                )
                //Albums
                albumDao.insertEntriesWithRelations(
                    artist.topAlbums,
                    RelationMapper.forLists().invoke(artist.topAlbums.map { Pair(artist, it) })
                )
                //Artist
                artistDao.insertEntriesWithRelations(
                    artist.similarArtists,
                    RelationMapper.forLists().invoke(artist.similarArtists.map { Pair(artist, it) })
                )
            }
        )
    ).build().stream(StoreRequest.cached(id, true))

    fun getTrackInfo(track: Track) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { track: Track ->
            val result =
                service.getTrackInfo(track.artist, track.name, lastFmAuthProvider.session!!.key)
                    .map()
            result
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { key ->
                trackDao.getTrack(key.id, key.artist).mapLatest { it?.map() }
            },
            writer = { _: Track, track ->
                trackDao.forceInsert(track)
                track.album?.let {
                    albumDao.insert(
                        Album(
                            name = it,
                            artist = track.artist,
                            url = "https://www.last.fm/music/${track.artist}/${it}"
                        )
                    )
                }
            }
        )
    ).build().stream(StoreRequest.cached(track, true))


    private suspend fun refreshImageUrl(localImageUrl: String?, listing: ListingMin) {
        if (localImageUrl.isNullOrBlank()) {
            println("Refreshing ImageURl for ${listing.name}")
            val url: String? = when (listing) {
                is Artist -> provideSpotifyService(
                    spotifyAuthProvider,
                    spotifyAuthenticator
                ).searchArtist(listing.name).maxBy { item ->
                    item.popularity
                }?.images?.first()?.url
                is Track -> ""
                else -> TODO()
            }
            println("ImageUrl $url")
            url?.let {
                listing.imageUrl = url
            }
        }
    }
}