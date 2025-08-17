package com.example.pokemon.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int
)