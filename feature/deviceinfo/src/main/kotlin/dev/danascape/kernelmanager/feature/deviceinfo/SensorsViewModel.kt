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
import dev.danascape.kernelmanager.core.model.SensorInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SensorsUiState(
    val deviceName: String? = null,
    val sensors: List<SensorInfo> = emptyList(),
)

/** Owns the sensor roster. Deliberately does not touch vitals: it shows none. */
class SensorsViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SensorsUiState())
    val state: StateFlow<SensorsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(deviceName = profile.identity.displayName) }
        }
        viewModelScope.launch {
            val details = deviceRepository.details()
            _state.update { it.copy(sensors = details.sensors) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                    SensorsViewModel(deviceRepository = container.deviceRepository)
                }
            }
    }
}
