package com.example.pokemon.data.database

import androidx.room.TypeConverter

class PokemonTypeConverter {
    @TypeConverter
    fun fromString(value: String) : List<String> {
        return value.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}