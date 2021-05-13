package de.schnettler.scrobbler.charts.repo.tools

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Suppress("UnnecessaryAbstractClass")
abstract class ApiTestUtils {
    protected val moshi = Moshi.Builder().build()

    protected fun getJsonStringFromFile(fileName: String) =
        this::class.java.classLoader?.getResource("$fileName.json")?.openStream()?.bufferedReader()?.readText()

    inline fun <reified T> Moshi.parseJsonObject(input: String) = adapter(T::class.java).fromJson(input)

    inline fun <reified T> Moshi.parseJsonList(input: String?): List<T>? {
        if (input == null) return null
        val adapter: JsonAdapter<List<T>> = adapter(Types.newParameterizedType(List::class.java, T::class.java))
        return adapter.fromJson(input)
    }
}