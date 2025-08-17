package com.example.pokemon.domain.usecase

import androidx.paging.PagingData
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(
        searchQuery: String,
        typeQueries: Set<String>, // <-- ИЗМЕНЕНИЕ: String -> Set<String>
        sortColumn: String
    ): Flow<PagingData<Pokemon>> {
        return repository.getPokemons(
            searchQuery = searchQuery,
            typeQueries = typeQueries, // <-- ИЗМЕНЕНИЕ: Передаем Set
            sortColumn = sortColumn
        )
    }
}