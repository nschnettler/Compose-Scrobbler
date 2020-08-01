package de.schnettler.database.daos

import androidx.room.Dao
import de.schnettler.database.models.RelatedArtistEntry

@Dao
abstract class ArtistRelationDao : BaseDao<RelatedArtistEntry>