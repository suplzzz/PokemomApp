package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    operator fun invoke(id: Int): Flow<Pokemon?> {
        return repository.getPokemonDetail(id)
    }
}