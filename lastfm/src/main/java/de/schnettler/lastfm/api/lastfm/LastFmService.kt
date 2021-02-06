package de.schnettler.lastfm.api.lastfm

import de.schnettler.common.BuildConfig
import de.schnettler.lastfm.di.tag.SessionAuthentication

@Suppress("TooManyFunctions")
@SessionAuthentication
interface LastFmService {
    companion object {
        const val ENDPOINT = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = BuildConfig.LASTFM_API_KEY
        const val SECRET = BuildConfig.LASTFM_SECRET

        const val METHOD_ARTIST_ALBUMS = "artist.getTopAlbums"
        const val METHOD_ARTIST_TRACKS = "artist.getTopTracks"
    }
}