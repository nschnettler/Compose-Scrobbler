package de.schnettler.scrobble.work

import androidx.work.DelegatingWorkerFactory
import de.schnettler.repo.ScrobbleRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrobbleDelegationWorkerFactory @Inject constructor(
    val repo: ScrobbleRepository
): DelegatingWorkerFactory() {
    init {
        Timber.d("[Work] DelegationFactory init")
        addFactory(ScrobbleWorkerFactory(repo))
    }
}