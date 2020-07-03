package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.ChartRepository
import de.schnettler.repo.LocalRepository

class LocalViewModel @ViewModelInject constructor(
    private val repo: LocalRepository
) : ViewModel() {
    val data by lazy {
        repo.getData()
    }
}