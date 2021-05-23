/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schnettler.scrobbler.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OptIn(InternalCoroutinesApi::class)
abstract class ReduxViewModel<S> constructor(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    private val stateMutex = Mutex()

    val state: StateFlow<S>
        get() = _state.asStateFlow()

    protected suspend fun setState(reducer: S.() -> S) {
        stateMutex.withLock {
            _state.value = reducer(_state.value)
        }
    }
}