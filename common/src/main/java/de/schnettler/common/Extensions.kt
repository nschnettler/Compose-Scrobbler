package de.schnettler.common

inline fun <E : Any, T : Collection<E>> T?.whenNotEmpty(func: (T) -> Unit) {
    if (this != null && this.isNotEmpty()) {
        func(this)
    }
}