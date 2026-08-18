package dev.danascape.kernelmanager.feature.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.data.device.DeviceRepository
import dev.danascape.kernelmanager.core.di.appContainer
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MonitorUiState(
    val profile: DeviceProfile? = null,
    val vitals: Vitals? = null,
)

class MonitorViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {

    private val _state = MutableStateFlow(MonitorUiState())
    val state: StateFlow<MonitorUiState> = _state.asStateFlow()

    private var sampling: Job? = null

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
    }

    /**
     * Sampling runs only while the screen is on screen.
     *
     * Each pass already spends half a second measuring idle residency, so this
     * polls continuously rather than on a timer — the measurement window is the
     * interval. Leaving it running in the background would be a battery cost
     * for a screen nobody is looking at.
     */
    fun startSampling() {
        if (sampling?.isActive == true) return
        sampling = viewModelScope.launch {
            while (isActive) {
                val vitals = deviceRepository.vitals()
                _state.update { it.copy(vitals = vitals) }
            }
        }
    }

    fun stopSampling() {
        sampling?.cancel()
        sampling = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                MonitorViewModel(container.deviceRepository)
            }
        }
    }
}
