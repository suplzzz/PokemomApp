package com.example.pokemon.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pokemon.data.database.AppDatabase
import com.example.pokemon.data.database.model.PokemonEntity
import com.example.pokemon.data.database.model.RemoteKey
import com.example.pokemon.data.mappers.toEntity
import com.example.pokemon.data.network.ApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
    private val isFiltering: Boolean
) : RemoteMediator<Int, PokemonEntity>() {

    private val pokemonDao = appDatabase.pokemonDao()
    private val remoteKeyDao = appDatabase.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        val lastUpdated = remoteKeyDao.getCreationTime() ?: 0L

        return if ((System.currentTimeMillis() - lastUpdated) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        if (isFiltering && loadType != LoadType.REFRESH) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        return try {
            val pageToLoad: Int = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                    prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }
            val offset = (pageToLoad - 1) * state.config.pageSize

            val listResponse = apiService.getPokemonList(
                limit = state.config.pageSize,
                offset = offset
            )

            val pokemonItems = listResponse.results
            val endOfPaginationReached = listResponse.next == null

            val pokemonEntities = coroutineScope {
                val deferredDetails = pokemonItems.map { item ->
                    async {
                        val detailDto = apiService.getPokemonDetail(item.name)
                        detailDto.toEntity()
                    }
                }
                deferredDetails.awaitAll()
            }

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.clearAllPokemons()
                    remoteKeyDao.clearRemoteKeys()
                }

                val prevKey = if (pageToLoad == 1) null else pageToLoad - 1
                val nextKey = if (endOfPaginationReached) null else pageToLoad + 1

                val remoteKeys = pokemonEntities.map { pokemon ->
                    RemoteKey(id = pokemon.id, prevKey = prevKey, nextKey = nextKey)
                }

                pokemonDao.insertAll(pokemonEntities)
                remoteKeyDao.insertAll(remoteKeys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { pokemon -> remoteKeyDao.getRemoteKeyById(pokemon.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PokemonEntity>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { pokemon -> remoteKeyDao.getRemoteKeyById(pokemon.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PokemonEntity>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeyDao.getRemoteKeyById(id)
            }
        }
    }
}