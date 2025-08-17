package com.example.pokemon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// pokemon/{id}
@Serializable
data class PokemonDetailDto (
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val types: List<TypeResponseDto>,
    val stats: List<StatResponseDto>
)

// types
@Serializable
data class TypeResponseDto(
    val slot: Int,
    val type: TypeInfoDto
)

@Serializable
data class TypeInfoDto(
    val name: String,
    val url: String
)

// stats
@Serializable
data class StatResponseDto(
    @SerialName("base_stat")
    val baseStat: Int,
    val stat: StatInfoDto
)

@Serializable
data class StatInfoDto(
    val name: String
)

// sprites
@Serializable
data class SpritesDto(
    val other: OtherSpriteDto?
)

@Serializable
data class OtherSpriteDto(
    @SerialName("official-artwork")
    val officialArtwork: OfficialArtworkDto?
)

@Serializable
data class OfficialArtworkDto(
    @SerialName("front_default")
    val frontDefault: String?
)