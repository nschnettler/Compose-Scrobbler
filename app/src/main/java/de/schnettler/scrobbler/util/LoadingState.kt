package de.schnettler.scrobbler.util

import android.content.Context
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

sealed class LoadingState<T>(open val data: T? = null) {
    class Initial<T>: LoadingState<T>()
    data class Loading<T>(
            val origin: ResponseOrigin,
            val oldData: T? = null
    ) : LoadingState<T>(oldData)
    data class Data<T>(
            val origin: ResponseOrigin,
            val newData: T
    ): LoadingState<T>(newData)
    data class Error<T>(
            val errorMsg: String?,
            val exception: Throwable? = null,
            val oldData: T? = null
    ): LoadingState<T>(oldData)

    fun handleIfError(ctx: Context) {
        if (this is Error) {
            errorMsg?.let { ctx.toast(it) }
            exception?.let { Timber.e(it) }
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> MutableStateFlow<LoadingState<T>>.updateState(response: StoreResponse<T>) {
    value = when(response) {
        is StoreResponse.Data -> LoadingState.Data(origin = response.origin, newData = response.value)
        is StoreResponse.Loading -> LoadingState.Loading(origin = response.origin, oldData = value.data)
        is StoreResponse.Error.Message -> LoadingState.Error(
                errorMsg = response.errorMessageOrNull(),
                oldData = value.data
        )
        is StoreResponse.Error.Exception -> LoadingState.Error(
                errorMsg = response.errorMessageOrNull(),
                exception = response.error,
                oldData = value.data
        )
    }
}