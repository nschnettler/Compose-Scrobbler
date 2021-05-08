package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import de.schnettler.scrobbler.model.LastFmEntity.Artist

@Dao
abstract class ArtistDao : BaseDao<Artist>