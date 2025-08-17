package com.example.pokemon.ui.model

data class PokemonUiModel(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int
)