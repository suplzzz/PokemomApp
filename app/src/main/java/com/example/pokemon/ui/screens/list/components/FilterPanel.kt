package com.example.pokemon.ui.screens.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pokemon.ui.screens.list.PokemonListUiState
import com.example.pokemon.ui.theme.getColorForType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterPanel(
    uiState: PokemonListUiState,
    onSortByChanged: (String) -> Unit,
    onTypeChanged: (String) -> Unit,
    onApplyClicked: () -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionHeader(title = "Sort by", onResetClick = onResetClicked)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SortChip("Number", "id", uiState.pendingSortBy, onSortByChanged)
            SortChip("Name", "name", uiState.pendingSortBy, onSortByChanged)
            SortChip("HP", "hp", uiState.pendingSortBy, onSortByChanged)
            SortChip("Attack", "attack", uiState.pendingSortBy, onSortByChanged)
            SortChip("Defense", "defense", uiState.pendingSortBy, onSortByChanged)
        }

        SectionHeader(title = "Filter by Type")
        val pokemonTypes = listOf(
            "normal", "fire", "water", "electric", "grass", "ice", "fighting",
            "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
            "dragon", "dark", "steel", "fairy"
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            pokemonTypes.forEach { type ->
                FilterChip(
                    selected = uiState.pendingTypes.contains(type),
                    onClick = { onTypeChanged(type) },
                    label = { Text(text = type.replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getColorForType(type),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Button(
            onClick = onApplyClicked,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Apply Filters")
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onResetClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (onResetClick != null) {
            IconButton(onClick = onResetClick) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Filters")
            }
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    value: String,
    currentSelection: String,
    onClick: (String) -> Unit
) {
    Surface(
        onClick = { onClick(value) },
        shape = CircleShape,
        color = if (currentSelection == value) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (currentSelection == value) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

