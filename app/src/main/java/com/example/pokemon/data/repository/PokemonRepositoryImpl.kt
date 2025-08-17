package com.example.pokemon.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.pokemon.data.PokemonRemoteMediator
import com.example.pokemon.data.database.AppDatabase
import com.example.pokemon.data.mappers.toDomain
import com.example.pokemon.data.network.ApiService
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : PokemonRepository {

    private val pokemonDao = appDatabase.pokemonDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getPokemons(
        searchQuery: String,
        typeQueries: Set<String>,
        sortColumn: String
    ): Flow<PagingData<Pokemon>> {

        val pagingSourceFactory = {
            pokemonDao.getPokemons(
                searchQuery = searchQuery,
                typeQueries = typeQueries,
                sortColumn = sortColumn
            )
        }

        val isFiltering = searchQuery.isNotEmpty() ||
                typeQueries.isNotEmpty() ||
                sortColumn != "id"
        return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        remoteMediator = PokemonRemoteMediator(
            appDatabase = appDatabase,
            apiService = apiService,
            isFiltering = isFiltering
        ),
        pagingSourceFactory = pagingSourceFactory
        ).flow.map  { pagingDataEntity ->
            pagingDataEntity.map { pokemonEntity ->
                pokemonEntity.toDomain()
            }
        }
    }

    override fun getPokemonDetail(id: Int): Flow<Pokemon?> {
        return pokemonDao.getPokemonById(id).map { pokemonEntity ->
            pokemonEntity?.toDomain()
        }
    }
}