package de.schnettler.lastfm.api

import com.serjltt.moshi.adapters.Wrapped
import de.schnettler.lastfm.models.RelationDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicBrainzService {
    companion object {
        const val ENDPOINT = "https://musicbrainz.org/ws/2/"
    }

    /*
     * Trending Shows
     */
    @GET("artist/{mbid}?inc=url-rels&fmt=json")
    @Wrapped(path = ["relations"])
    suspend fun getArtistRelations(@Path("mbid") mbid: String ): List<RelationDto>


    fun findImageUrl(relations: List<RelationDto>): String? {
        relations.forEach {relation ->
            if (relation.type == "image") {
                val url = relation.url.resource
                if (url.startsWith("https://commons.wikimedia.org/wiki/File:")) {
                    val filename = url.substring(url.lastIndexOf("/") + 1)
                    return "https://commons.wikimedia.org/wiki/Special:Redirect/file/$filename"
                }
            }
        }
        return null
    }
}