package de.schnettler.scrobbler.charts.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.charts.model.ChartTrackResponse

class TestApi : ChartApi {
    private val moshi = Moshi.Builder().build()

    private fun getJsonStringFromFile(fileName: String) =
        this::class.java.classLoader?.getResource("$fileName.json")?.openStream()?.bufferedReader()?.readText()

    inline fun <reified T> Moshi.parseJsonObject(input: String) = adapter(T::class.java).fromJson(input)

    inline fun <reified T> Moshi.parseJsonList(input: String?): List<T>? {
        if (input == null) return null
        val adapter: JsonAdapter<List<T>> = adapter(Types.newParameterizedType(List::class.java, T::class.java))
        return adapter.fromJson(input)
    }

    override suspend fun getTopArtists(): List<ChartArtistResponse> {
        return moshi.parseJsonList(getJsonStringFromFile("topArtists")) ?: emptyList()
    }

    override suspend fun getTopTracks(): List<ChartTrackResponse> {
        TODO("Not yet implemented")
    }
}