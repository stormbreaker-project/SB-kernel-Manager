// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.data.device.DeviceRepository
import dev.danascape.kernelmanager.core.di.appContainer
import dev.danascape.kernelmanager.core.model.DeviceDetails
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceInfoUiState(
    val profile: DeviceProfile? = null,
    val details: DeviceDetails? = null,
    val vitals: Vitals? = null,
)

class DeviceInfoViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DeviceInfoUiState())
    val state: StateFlow<DeviceInfoUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val details = deviceRepository.details()
            _state.update { it.copy(details = details) }
        }
        viewModelScope.launch {
            val vitals = deviceRepository.vitals()
            _state.update { it.copy(vitals = vitals) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                    DeviceInfoViewModel(deviceRepository = container.deviceRepository)
                }
            }
    }
}
