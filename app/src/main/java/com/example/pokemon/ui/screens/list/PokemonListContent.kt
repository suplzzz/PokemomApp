package com.example.pokemon.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.pokemon.ui.model.PokemonUiModel
import com.example.pokemon.ui.screens.list.components.PokemonGrid
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    pokemons: LazyPagingItems<PokemonUiModel>,
    isFiltering: Boolean,
    onCharacterClick: (Int) -> Unit,
    lazyGridState: LazyGridState
) {
    val isPagingRefreshing = pokemons.loadState.refresh is LoadState.Loading
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pokemons.loadState) {
        if (pokemons.loadState.refresh is LoadState.Error && pokemons.itemCount > 0) {
            val error = (pokemons.loadState.refresh as LoadState.Error).error
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Failed to refresh: ${error.localizedMessage}",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(contentPadding)
                    .clip(RoundedCornerShape(32.dp))
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isPagingRefreshing,
            onRefresh = { pokemons.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val refreshState = pokemons.loadState.refresh
                val itemCount = pokemons.itemCount

                when {
                    refreshState is LoadState.Loading && itemCount == 0 -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    refreshState is LoadState.Error && itemCount == 0 -> {
                        val error = refreshState.error
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Error: ${error.localizedMessage ?: "Unknown error"}")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { pokemons.retry() }) { Text("Retry") }
                        }
                    }
                    refreshState is LoadState.NotLoading && itemCount == 0 -> {
                        Text(
                            "No pokémons found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        PokemonGrid(
                            contentPadding = contentPadding,
                            pokemons = pokemons,
                            lazyGridState = lazyGridState,
                            isFiltering = isFiltering,
                            onCharacterClick = onCharacterClick
                        )
                    }
                }
            }
        }
    }
}