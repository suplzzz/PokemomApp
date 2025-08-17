package com.example.pokemon.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokemon.data.database.dao.PokemonDao
import com.example.pokemon.data.database.dao.RemoteKeyDao
import com.example.pokemon.data.database.model.PokemonEntity
import com.example.pokemon.data.database.model.RemoteKey

@Database(
    entities = [PokemonEntity::class, RemoteKey::class],
    version = 1,
)
@TypeConverters(PokemonTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}