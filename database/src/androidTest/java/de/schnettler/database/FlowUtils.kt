package de.schnettler.database

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend inline fun <T> Flow<T?>.collectValue(crossinline action: suspend (value: T?) -> Unit) {
    this.take(1).collect {
        action(it)
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend inline fun <T> Flow<T>.collectValues(
    number: Int,
    crossinline action: suspend (value: T) -> Unit
) {
    this.take(number).collect {
        action(it)
    }
}