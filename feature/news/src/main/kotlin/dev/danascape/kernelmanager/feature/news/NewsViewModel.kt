// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import dev.danascape.kernelmanager.core.di.appContainer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.data.news.NewsRepository
import dev.danascape.kernelmanager.core.common.DataResult
import dev.danascape.kernelmanager.core.common.LoadError
import dev.danascape.kernelmanager.core.model.NewsPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NewsUiState {
    data object Loading : NewsUiState

    /** Loaded and empty is a real state, not a failure. */
    data object Empty : NewsUiState

    /** @param stale served from cache because the network was unreachable. */
    data class Ready(
        val posts: List<NewsPost>,
        val stale: Boolean,
    ) : NewsUiState

    data class Failed(
        val error: LoadError,
    ) : NewsUiState
}

class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            // Only blank the screen when there is nothing to keep showing;
            // a retry over existing posts should not flash empty.
            if (_state.value !is NewsUiState.Ready) {
                _state.value = NewsUiState.Loading
            }
            _state.value =
                when (val result = repository.news()) {
                    is DataResult.Success -> {
                        if (result.data.isEmpty()) {
                            NewsUiState.Empty
                        } else {
                            NewsUiState.Ready(result.data, result.stale)
                        }
                    }

                    is DataResult.Failure -> {
                        NewsUiState.Failed(result.error)
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                    NewsViewModel(container.newsRepository)
                }
            }
    }
}
