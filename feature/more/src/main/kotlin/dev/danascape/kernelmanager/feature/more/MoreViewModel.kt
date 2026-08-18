package dev.danascape.kernelmanager.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import dev.danascape.kernelmanager.core.di.appContainer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.danascape.kernelmanager.core.data.links.LinksRepository
import dev.danascape.kernelmanager.core.model.ThemePreference
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.model.LinkSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MoreUiState(
    val sections: List<LinkSection> = emptyList(),
    val theme: ThemePreference = ThemePreference.SYSTEM,
)

class MoreViewModel(
    private val linksRepository: LinksRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoreUiState())
    val state: StateFlow<MoreUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(sections = linksRepository.links())
        }
        viewModelScope.launch {
            themeRepository.theme.collect { theme ->
                _state.value = _state.value.copy(theme = theme)
            }
        }
    }

    fun setTheme(preference: ThemePreference) {
        viewModelScope.launch { themeRepository.setTheme(preference) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                MoreViewModel(
                    linksRepository = container.linksRepository,
                    themeRepository = container.themeRepository,
                )
            }
        }
    }
}
