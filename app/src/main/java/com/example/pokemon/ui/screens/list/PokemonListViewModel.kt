package com.example.pokemon.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pokemon.domain.usecase.GetPokemonsUseCase
import com.example.pokemon.ui.mappers.toUiModel
import com.example.pokemon.ui.model.PokemonUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ListScreenEvent {
    data object ScrollToTop : ListScreenEvent()
}

data class PokemonListUiState(
    val searchQuery: String = "",
    val appliedSortBy: String = "id",
    val appliedTypes: Set<String> = emptySet(),
    val pendingSortBy: String = "id",
    val pendingTypes: Set<String> = emptySet()
) {
    val isFiltering: Boolean
        get() = searchQuery.isNotEmpty() || appliedTypes.isNotEmpty()
}

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val getPokemonsUseCase: GetPokemonsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ListScreenEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemons: Flow<PagingData<PokemonUiModel>> = _uiState
        .distinctUntilChanged { old, new ->
            old.searchQuery == new.searchQuery &&
                    old.appliedSortBy == new.appliedSortBy &&
                    old.appliedTypes == new.appliedTypes
        }
        .flatMapLatest { state ->
            getPokemonsUseCase(
                searchQuery = state.searchQuery,
                typeQueries = state.appliedTypes,
                sortColumn = state.appliedSortBy
            ).map { pagingData ->
                pagingData.map { pokemonDomain ->
                    pokemonDomain.toUiModel()
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        sendScrollToTopEvent()
    }

    fun onPendingSortByChanged(sortBy: String) {
        _uiState.update { it.copy(pendingSortBy = sortBy) }
    }

    fun onPendingTypeChanged(type: String) {
        _uiState.update { currentState ->
            val newTypes = currentState.pendingTypes.toMutableSet()
            if (newTypes.contains(type)) {
                newTypes.remove(type)
            } else {
                newTypes.add(type)
            }
            currentState.copy(pendingTypes = newTypes)
        }
    }

    fun resetPendingFilters() {
        _uiState.update {
            it.copy(pendingSortBy = "id", pendingTypes = emptySet())
        }
    }

    fun onFilterPanelOpened() {
        _uiState.update { it.copy(pendingSortBy = it.appliedSortBy, pendingTypes = it.appliedTypes) }
    }

    fun applyFilters() {
        _uiState.update { it.copy(appliedSortBy = it.pendingSortBy, appliedTypes = it.pendingTypes) }
        sendScrollToTopEvent()
    }

    private fun sendScrollToTopEvent() {
        viewModelScope.launch {
            _eventChannel.send(ListScreenEvent.ScrollToTop)
        }
    }
}