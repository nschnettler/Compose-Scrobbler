/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schnettler.scrobbler.core.ui.state

import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.model.Result
import de.schnettler.scrobbler.model.Result.Error
import de.schnettler.scrobbler.model.Result.Loading
import de.schnettler.scrobbler.model.Result.Success

/**
 * Immutable data class that allows for loading, data, and exception to be managed independently.
 *
 * This is useful for screens that want to show the last successful result while loading or a later
 * refresh has caused an error.
 */
data class UiState<T>(
    val loading: Boolean = false,
    val exception: Throwable? = null,
    val data: T? = null
) {
    /**
     * True if this contains an error
     */
    val hasError: Boolean
        get() = exception != null

    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = data == null && loading && !hasError
}

/**
 * Copy a UiState<T> based on a Result<T>.
 *
 * Result.Success will set all fields
 * Result.Error will reset loading and exception only
 */
fun <T> UiState<T>.copyWithResult(value: Result<T>): UiState<T> {
    return when (value) {
        is Success -> copy(loading = false, exception = null, data = value.data)
        is Error -> copy(loading = false, exception = value.exception)
        is Loading -> copy(loading = true)
    }
}

fun <S, R> UiState<S>.copyWithStoreResponse(response: StoreResponse<R>, reducer: (S?, R) -> S): UiState<S> {
    return when (response) {
        is StoreResponse.Loading -> copy(loading = true)
        is StoreResponse.Data -> copy(loading = false, exception = null, data = reducer(this.data, response.value))
        is StoreResponse.NoNewData -> copy(loading = false)
        is StoreResponse.Error.Exception -> copy(exception = response.error, loading = false)
        is StoreResponse.Error.Message -> copy(exception = Exception(response.message), loading = false)
    }
}
