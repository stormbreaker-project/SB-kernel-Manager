// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

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
import kotlin.math.abs

private const val MICROAMPS_PER_MILLIAMP = 1000f
private const val MILLIS_PER_SECOND = 1000f

private const val WINDOW = 60

/** Recent history, kept only while the screen is open. */
data class MetricHistory(
    val totalLoad: List<Float> = emptyList(),
    val perCoreLoad: List<List<Float>> = emptyList(),
    val memoryUsed: List<Float> = emptyList(),
    val batteryDrawMilliAmps: List<Float> = emptyList(),
    val networkBytesPerSecond: List<Float> = emptyList(),
)

data class MonitorUiState(
    val profile: DeviceProfile? = null,
    val vitals: Vitals? = null,
    val history: MetricHistory = MetricHistory(),
)

class MonitorViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MonitorUiState())
    val state: StateFlow<MonitorUiState> = _state.asStateFlow()

    private var sampling: Job? = null
    private var previousNetworkBytes: Long? = null
    private var previousUptimeMillis: Long? = null

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
    }

    /** Polls continuously: the idle-residency measurement window is the interval. */
    fun startSampling() {
        if (sampling?.isActive == true) return
        sampling =
            viewModelScope.launch {
                while (isActive) {
                    val vitals = deviceRepository.vitals()
                    _state.update { current ->
                        current.copy(vitals = vitals, history = current.history.plus(vitals))
                    }
                }
            }
    }

    fun stopSampling() {
        sampling?.cancel()
        sampling = null
        previousNetworkBytes = null
        previousUptimeMillis = null
    }

    private fun MetricHistory.plus(vitals: Vitals): MetricHistory {
        val cores = vitals.load?.perCore.orEmpty()
        return copy(
            totalLoad = totalLoad.append(vitals.load?.average),
            perCoreLoad =
                if (cores.isEmpty()) {
                    perCoreLoad
                } else {
                    List(cores.size) { core ->
                        perCoreLoad.getOrNull(core).orEmpty().append(cores[core])
                    }
                },
            memoryUsed = memoryUsed.append(vitals.memory?.usedFraction),
            batteryDrawMilliAmps =
                batteryDrawMilliAmps
                    .append(vitals.battery?.currentMicroAmps?.let { abs(it) / MICROAMPS_PER_MILLIAMP }),
            networkBytesPerSecond = networkBytesPerSecond.append(networkRate(vitals)),
        )
    }

    /** TrafficStats reports totals since boot, so the rate is a delta over elapsed time. */
    private fun networkRate(vitals: Vitals): Float? {
        val total = vitals.network?.let { it.rxBytes + it.txBytes } ?: return null
        val now = vitals.uptimeMillis
        val previousBytes = previousNetworkBytes
        val previousMillis = previousUptimeMillis
        previousNetworkBytes = total
        previousUptimeMillis = now

        if (previousBytes == null || previousMillis == null) return null
        val seconds = (now - previousMillis) / MILLIS_PER_SECOND
        if (seconds <= 0f) return null
        return ((total - previousBytes).coerceAtLeast(0) / seconds)
    }

    private fun List<Float>.append(value: Float?): List<Float> {
        if (value == null) return this
        val next = this + value
        return if (next.size > WINDOW) next.takeLast(WINDOW) else next
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                    MonitorViewModel(container.deviceRepository)
                }
            }
    }
}
