package de.schnettler.scrobble.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import de.schnettler.repo.ScrobbleRepository

class ScrobbleWorkerFactory(private val repo: ScrobbleRepository): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            ScrobbleWorker::class.java.name ->
                ScrobbleWorker(
                    appContext,
                    workerParameters,
                    repo
                )
            else -> null
        }
    }
}