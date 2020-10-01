package de.schnettler.repo

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.schnettler.common.TimePeriod
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.lastfm.models.ArtistTracksDto
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.lastfm.models.DateDto
import de.schnettler.lastfm.models.ImageDto
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.ResponseInfo
import de.schnettler.lastfm.models.SearchResultDto
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.lastfm.models.UserArtistResponse
import de.schnettler.lastfm.models.UserDto
import de.schnettler.lastfm.models.UserTrackDto

class TestService : LastFmService {

    private val moshi = Moshi.Builder().build()

    private fun getJsonStringFromFile(fileName: String) =
        this::class.java.classLoader?.getResource("$fileName.json")?.openStream()?.bufferedReader()?.readText()

    inline fun <reified T> Moshi.parseJsonObject(input: String) = adapter(T::class.java).fromJson(input)

    inline fun <reified T> Moshi.parseJsonList(input: String?): List<T>? {
        if (input == null) return null
        val adapter: JsonAdapter<List<T>> = adapter(Types.newParameterizedType(List::class.java, T::class.java))
        return adapter.fromJson(input)
    }

    override suspend fun getTopArtists(): List<ChartArtistDto> {
        return moshi.parseJsonList(getJsonStringFromFile("topArtists")) ?: emptyList()
    }

    override suspend fun getTopTracks(): List<UserTrackDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getSession(token: String, signature: String): SessionDto {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(sessionKey: String): UserDto {
        return UserDto(
            "ShadowSoul",
            "https://www.last.fm/user/Sh4dowSoul",
            8308,
            "Germany",
            22,
            "Niklas Schnettler",
            DateDto(100000),
            listOf(ImageDto("Large", "url"))
        )
    }

    override suspend fun getUserTopAlbums(timePeriod: TimePeriod, sessionKey: String): List<AlbumDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserTopArtists(timePeriod: TimePeriod, sessionKey: String): UserArtistResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getUserLikedTracksAmount(sessionKey: String): ResponseInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getUserTopTracks(timePeriod: TimePeriod, sessionKey: String): List<UserTrackDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRecentTrack(sessionKey: String): List<RecentTracksDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistInfo(name: String, sessionKey: String): ArtistInfoDto {
        TODO("Not yet implemented")
    }

    override suspend fun getTrackInfo(artistName: String, trackName: String, sessionKey: String): TrackInfoDto {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistAlbums(name: String): List<AlbumDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistTracks(name: String): List<ArtistTracksDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumInfo(artistName: String, albumName: String, sessionKey: String): AlbumInfoDto {
        TODO("Not yet implemented")
    }

    override suspend fun searchArtist(query: String, limit: Long): List<SearchResultDto> {
        TODO("Not yet implemented")
    }

    override suspend fun searchAlbum(query: String, limit: Long): List<SearchResultDto> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTrack(query: String, limit: Long): List<SearchResultDto> {
        TODO("Not yet implemented")
    }
}