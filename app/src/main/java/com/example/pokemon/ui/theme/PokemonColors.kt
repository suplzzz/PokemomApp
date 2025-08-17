package com.example.pokemon.ui.theme

import androidx.compose.ui.graphics.Color

object PokemonColors {
    val Bug = Color(0xFF92BC2C)
    val Dark = Color(0xFF5A5366)
    val Dragon = Color(0xFF0A6DC4)
    val Electric = Color(0xFFF2D94E)
    val Fairy = Color(0xFFEC8FE6)
    val Fighting = Color(0xFFCE4069)
    val Fire = Color(0xFFE85C4F)
    val Flying = Color(0xFF90A7DA)
    val Ghost = Color(0xFF5269AC)
    val Grass = Color(0xFF4E807A)
    val Ground = Color(0xFFD9775E)
    val Ice = Color(0xFF74CEC0)
    val Normal = Color(0xFF9099A1)
    val Poison = Color(0xFFB763CF)
    val Psychic = Color(0xFFF67176)
    val Rock = Color(0xFFC7B78B)
    val Steel = Color(0xFF5A8EA1)
    val Water = Color(0xFF5090D5)

    val Default = Color(0xFF6C7986)
}

fun getColorForType(typeName: String): Color {
    return when (typeName.lowercase()) {
        "bug" -> PokemonColors.Bug
        "dark" -> PokemonColors.Dark
        "dragon" -> PokemonColors.Dragon
        "electric" -> PokemonColors.Electric
        "fairy" -> PokemonColors.Fairy
        "fighting" -> PokemonColors.Fighting
        "fire" -> PokemonColors.Fire
        "flying" -> PokemonColors.Flying
        "ghost" -> PokemonColors.Ghost
        "grass" -> PokemonColors.Grass
        "ground" -> PokemonColors.Ground
        "ice" -> PokemonColors.Ice
        "normal" -> PokemonColors.Normal
        "poison" -> PokemonColors.Poison
        "psychic" -> PokemonColors.Psychic
        "rock" -> PokemonColors.Rock
        "steel" -> PokemonColors.Steel
        "water" -> PokemonColors.Water
        else -> PokemonColors.Default
    }
}