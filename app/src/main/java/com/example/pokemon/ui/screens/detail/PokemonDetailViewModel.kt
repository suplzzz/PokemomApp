package com.example.pokemon.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.usecase.GetPokemonDetailUseCase
import com.example.pokemon.ui.mappers.toUiModel
import com.example.pokemon.ui.model.PokemonUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PokemonDetailUiState {
    data object Loading : PokemonDetailUiState()
    data class Success(val pokemon: PokemonUiModel) : PokemonDetailUiState()
    data class Error(val message: String?) : PokemonDetailUiState()
}

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle["pokemonId"])

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadPokemonDetails()
    }

    fun loadPokemonDetails() {
        viewModelScope.launch {
            getPokemonDetailUseCase(pokemonId)
                .onStart {
                    _uiState.value = PokemonDetailUiState.Loading
                }
                .catch { exception ->
                    _uiState.value = PokemonDetailUiState.Error(exception.message)
                }
                .collect { pokemonDomain ->
                    if (pokemonDomain != null) {
                        _uiState.value = PokemonDetailUiState.Success(pokemonDomain.toUiModel())
                    } else {
                        _uiState.value = PokemonDetailUiState.Error("Pokémon not found.")
                    }
                }
        }
    }
}