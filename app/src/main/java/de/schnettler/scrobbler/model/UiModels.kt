package de.schnettler.scrobbler.model

import androidx.ui.graphics.vector.VectorAsset
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.Screen
import kotlinx.coroutines.flow.MutableStateFlow

data class ListItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

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