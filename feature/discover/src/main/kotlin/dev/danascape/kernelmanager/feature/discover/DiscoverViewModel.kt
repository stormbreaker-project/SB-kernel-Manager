// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.common.DataResult
import dev.danascape.kernelmanager.core.data.device.DeviceRepository
import dev.danascape.kernelmanager.core.data.news.NewsRepository
import dev.danascape.kernelmanager.core.di.appContainer
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.NewsPost
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val HEADLINE_COUNT = 3

data class DiscoverUiState(
    val profile: DeviceProfile? = null,
    val vitals: Vitals? = null,
    val headlines: List<NewsPost> = emptyList(),
)

class DiscoverViewModel(
    private val deviceRepository: DeviceRepository,
    private val newsRepository: NewsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        // These land at very different times — the profile shells out to
        // getprop, vitals spends half a second sampling idle residency, news
        // hits the network. update() is a compare-and-set loop, so a slow
        // writer cannot overwrite a field a faster one already filled in.
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val vitals = deviceRepository.vitals()
            _state.update { it.copy(vitals = vitals) }
        }
        viewModelScope.launch {
            val result = newsRepository.news()
            if (result is DataResult.Success) {
                _state.update { it.copy(headlines = result.data.take(HEADLINE_COUNT)) }
            }
            // A failed news load leaves the section absent rather than putting an
            // error on a screen whose job is the device in front of you.
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                    DiscoverViewModel(
                        deviceRepository = container.deviceRepository,
                        newsRepository = container.newsRepository,
                    )
                }
            }
    }
}
