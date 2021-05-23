package de.schnettler.scrobbler.submission.db

import androidx.room.Dao
import de.schnettler.scrobbler.persistence.dao.BaseDao
import de.schnettler.scrobbler.submission.model.SubmissionFailureEntity

@Dao
abstract class SubmissionFailureDao : BaseDao<SubmissionFailureEntity> {

}