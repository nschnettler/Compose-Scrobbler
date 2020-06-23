package de.schnettler.scrobbler.model

import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

data class LoadingState<T>(
    val data: T? = null,
    val loading: Boolean = false,
    val error: String = ""
)

fun <T> MutableStateFlow<LoadingState<T>?>.update(response: StoreResponse<T>) {
    val oldValue = this.value ?: LoadingState()
    when(response) {
        is StoreResponse.Data -> {
            this.value = oldValue.copy(data = response.value, loading = false, error = "")
        }
        is StoreResponse.Loading -> {
            this.value = oldValue.copy(data = oldValue.data, loading = true, error = "")
        }
        is StoreResponse.Error -> {
            this.value = oldValue.copy(data = oldValue.data, loading = false, error = response.errorMessageOrNull() ?: "undef")
        }
    }
}

sealed class LoadingState2<T>(open val data: T? = null) {
    class Initial<T>(): LoadingState2<T>()
    data class Loading<T>(val origin: ResponseOrigin, val oldData: T? = null) : LoadingState2<T>(oldData)
    data class Data<T>(val origin: ResponseOrigin, val newData: T): LoadingState2<T>(newData)
    data class Error<T>(val errorMsg: String, val oldData: T? = null): LoadingState2<T>(oldData)
}

@ExperimentalCoroutinesApi
fun <T> MutableStateFlow<LoadingState2<T>>.update2(response: StoreResponse<T>) {
    value = when(response) {
        is StoreResponse.Data -> LoadingState2.Data(origin = response.origin, newData = response.value)
        is StoreResponse.Loading -> LoadingState2.Loading(origin = response.origin,oldData = value.data)
        is StoreResponse.Error -> LoadingState2.Error(errorMsg = response.errorMessageOrNull() ?: "", oldData = value.data)
    }
}