package dev.danascape.kernelmanager.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import dev.danascape.kernelmanager.core.di.appContainer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.batterymonitor.BatterySessionStore
import dev.danascape.kernelmanager.core.data.links.LinksRepository
import dev.danascape.kernelmanager.core.model.ThemePreference
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.model.LinkSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MoreUiState(
    val sections: List<LinkSection> = emptyList(),
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val batteryMonitorEnabled: Boolean = false,
)

class MoreViewModel(
    private val linksRepository: LinksRepository,
    private val themeRepository: ThemeRepository,
    private val batteryStore: BatterySessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow(MoreUiState())
    val state: StateFlow<MoreUiState> = _state.asStateFlow()

    init {
        // Same reason as Discover: the links load and the theme flow write
        // concurrently, and read-modify-write on .value loses one of them.
        viewModelScope.launch {
            val sections = linksRepository.links()
            _state.update { it.copy(sections = sections) }
        }
        viewModelScope.launch {
            themeRepository.theme.collect { theme ->
                _state.update { it.copy(theme = theme) }
            }
        }
        viewModelScope.launch {
            batteryStore.enabled.collect { enabled ->
                _state.update { it.copy(batteryMonitorEnabled = enabled) }
            }
        }
    }

    fun setTheme(preference: ThemePreference) {
        viewModelScope.launch { themeRepository.setTheme(preference) }
    }

    /** Only records the preference; starting the service is the UI's job. */
    fun setBatteryMonitorEnabled(enabled: Boolean) {
        viewModelScope.launch { batteryStore.setEnabled(enabled) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                MoreViewModel(
                    linksRepository = container.linksRepository,
                    themeRepository = container.themeRepository,
                    batteryStore = container.batterySessionStore,
                )
            }
        }
    }
}
