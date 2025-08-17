package com.example.pokemon.data.mappers

import com.example.pokemon.data.database.model.PokemonEntity
import com.example.pokemon.data.dto.PokemonDetailDto
import com.example.pokemon.domain.model.Pokemon

fun PokemonDetailDto.toEntity(): PokemonEntity {
    val imageUrl = this.sprites.other?.officialArtwork?.frontDefault ?: ""

    val hp = this.stats.find { it.stat.name == "hp" }?.baseStat ?: 0
    val attack = this.stats.find { it.stat.name == "attack" }?.baseStat ?: 0
    val defense = this.stats.find { it.stat.name == "defense" }?.baseStat ?: 0

    val types = this.types.map { it.type.name }

    return PokemonEntity(
        id = this.id,
        name = this.name,
        imageUrl = imageUrl,
        types = types,
        hp = hp,
        attack = attack,
        defense = defense
    )
}

fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        types = this.types,
        hp = this.hp,
        attack = this.attack,
        defense = this.defense
    )
}