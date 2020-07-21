package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.RelationshipDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.lastfm.LastFmService
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
    val artistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: String ->
            val response = service.getArtistInfo(key, lastFmAuthProvider.getSessionKeyOrThrow())
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
                trackDao.insertOrUpdateStats(artist.topTracks)
                trackDao.insertRelations(RelationMapper.forLists().invoke(artist.topTracks.map { Pair(artist, it) }))
                //Albums
                albumDao.insertOrUpdateStats(artist.topAlbums)
                albumDao.insertRelations(RelationMapper.forLists().invoke(artist.topAlbums.map { Pair(artist, it) }))
                //Artist
                artistDao.insertEntriesWithRelations(
                    artist.similarArtists,
                    RelationMapper.forLists().invoke(artist.similarArtists.map { Pair(artist, it) })
                )
            }
        )
    ).build()

    val trackStore =  StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: CommonTrack ->
            service.getTrackInfo(key.artist, key.name, lastFmAuthProvider.getSessionKeyOrThrow())
                    .map()
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { key ->
                trackDao.getTrack(key.id, key.artist).mapLatest { it?.map() }
            },
            writer = { _: CommonTrack, value ->
                trackDao.forceInsert(value)
                value.album?.let {
                    albumDao.insert(
                        Album(
                            name = it,
                            artist = value.artist,
                            url = "https://www.last.fm/music/${value.artist}/${it}"
                        )
                    )
                }
            }
        )
    ).build()


    val albumStore = StoreBuilder.from<Album, Album, Album>(
        fetcher = nonFlowValueFetcher { key: Album ->
            service.getAlbumInfo(artistName = key.getArtistOrThrow(),
                albumName = key.id, sessionKey = lastFmAuthProvider.getSessionKeyOrThrow()).map()
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key ->
                albumDao.getAlbum(id = key.id,
                    artistId = key.getArtistOrThrow()
                ).combine(trackDao.getAlbumTracks(key.name, key.getArtistOrThrow())) {album, tracks ->
                    album?.tracks = tracks
                    return@combine album
                }
            },
            writer = {key, value ->
                albumDao.forceInsert(value)
                //TODO: DONT FORCE INSERT, ONLY UPDATE ALBUM
                trackDao.forceInsertAll(value.tracks)
            }
        )
    ).build()

    private suspend fun refreshImageUrl(localImageUrl: String?, listing: LastFmEntity) {
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