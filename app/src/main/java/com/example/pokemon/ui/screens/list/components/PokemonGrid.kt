package com.example.pokemon.ui.screens.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.pokemon.ui.model.PokemonUiModel

@Composable
fun PokemonGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    pokemons: LazyPagingItems<PokemonUiModel>,
    lazyGridState: LazyGridState,
    isFiltering: Boolean,
    onCharacterClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = pokemons.itemCount,
            key = pokemons.itemKey { it.id }
        ) { index ->
            pokemons[index]?.let { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    onClick = { onCharacterClick(pokemon.id) }
                )
            }
        }

        if (pokemons.loadState.append is LoadState.Loading && !isFiltering) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}