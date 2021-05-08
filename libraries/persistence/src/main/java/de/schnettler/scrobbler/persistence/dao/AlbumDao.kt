package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import de.schnettler.scrobbler.model.LastFmEntity.Album

@Dao
abstract class AlbumDao : BaseDao<Album>