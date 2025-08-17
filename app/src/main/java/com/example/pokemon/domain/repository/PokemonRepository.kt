package com.example.pokemon.domain.repository

import androidx.paging.PagingData
import com.example.pokemon.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    fun getPokemons(
        searchQuery: String,
        typeQueries: Set<String>,
        sortColumn: String
    ): Flow<PagingData<Pokemon>>

    fun getPokemonDetail(id: Int): Flow<Pokemon?>
}