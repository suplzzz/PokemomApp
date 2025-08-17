package com.example.pokemon.ui.mappers

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.ui.model.PokemonUiModel

fun Pokemon.toUiModel(): PokemonUiModel {
    return PokemonUiModel(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        types = this.types,
        hp = this.hp,
        attack = this.attack,
        defense = this.defense
    )
}