package de.schnettler.database.daos

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.DataGenerator
import de.schnettler.database.collectValue
import de.schnettler.database.models.RelatedArtistEntry
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ArtistRelationDaoTest : DatabaseTest() {

    @Test
    fun getRelatedArtists_relationsPresent_returnsSortedListOfArtists() = runBlockingTest {
        // Given - Artists and relations in db
        val artists = DataGenerator.generateArtists(10)
        val testedArtist = artists.first()
        val relations = listOf(
            RelatedArtistEntry(testedArtist.id, artists[7].id, 2),
            RelatedArtistEntry(testedArtist.id, artists[4].id, 0),
            RelatedArtistEntry(testedArtist.id, artists[6].id, 1),
            RelatedArtistEntry(artists[5].id, artists[3].id, 0)
        )
        val testedRelations = relations.subList(0, 3).sortedBy { it.orderIndex }
        db.artistDao().insertAll(artists)
        db.relationDao().insertAll(relations)

        // WHEN - Requesting related Artists of an Artist with related Artists
        val relatedArtists = db.relationDao().getRelatedArtists(testedArtist.id)

        // THEN - Returns correct number of related Artists, sorted by orderIndex ASC
        relatedArtists.collectValue { result ->
            expect(result).notToBeNull().hasSize(3)
            expect(result?.map { it.relation }).notToBeNull().containsExactlyElementsOf(testedRelations)
        }
    }

    @Test
    fun getRelatedArtists_unknownArtist_returnsEmptyList() = runBlockingTest {
        // Given - Artists and relations in db
        val artists = DataGenerator.generateArtists(10)
        val relations = listOf(
            RelatedArtistEntry(artists.first().id, artists[7].id, 2),
            RelatedArtistEntry(artists.first().id, artists[4].id, 0),
            RelatedArtistEntry(artists.first().id, artists[6].id, 1),
            RelatedArtistEntry(artists[5].id, artists[3].id, 0)
        )
        db.artistDao().insertAll(artists)
        db.relationDao().insertAll(relations)

        // WHEN - Requesting related Artists of an artist not in db
        val relatedArtists = db.relationDao().getRelatedArtists("UnknownArtist")

        // THEN - Returns an empty list
        relatedArtists.collectValue { result ->
            expect(result).notToBeNull().isEmpty()
        }
    }
}