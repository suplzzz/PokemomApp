package com.example.pokemon.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int
)