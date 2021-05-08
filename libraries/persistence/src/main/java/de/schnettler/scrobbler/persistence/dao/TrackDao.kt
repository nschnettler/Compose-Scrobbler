package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import de.schnettler.scrobbler.model.LastFmEntity.Track

@Dao
abstract class TrackDao : BaseDao<Track>