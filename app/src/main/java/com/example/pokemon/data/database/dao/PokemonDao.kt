package com.example.pokemon.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.pokemon.data.database.model.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(pokemons: List<PokemonEntity>)

    @RawQuery(observedEntities = [PokemonEntity::class])
    abstract fun getPokemonsRaw(query: SimpleSQLiteQuery): PagingSource<Int, PokemonEntity>

    fun getPokemons(
        searchQuery: String,
        typeQueries: Set<String>,
        sortColumn: String
    ): PagingSource<Int, PokemonEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM pokemons")
        val args = mutableListOf<Any>()
        var whereClauseAdded = false

        if (searchQuery.isNotEmpty()) {
            queryBuilder.append(" WHERE name LIKE ?")
            args.add("%$searchQuery%")
            whereClauseAdded = true
        }

        typeQueries.forEach { type ->
            if (whereClauseAdded) {
                queryBuilder.append(" AND")
            } else {
                queryBuilder.append(" WHERE")
                whereClauseAdded = true
            }
            queryBuilder.append(" types LIKE ?")
            args.add("%$type%")
        }

        queryBuilder.append(
            """
            ORDER BY
                CASE WHEN ? = 'id' THEN id END ASC,
                CASE WHEN ? = 'name' THEN name END ASC,
                CASE WHEN ? = 'hp' THEN hp END DESC,
                CASE WHEN ? = 'attack' THEN attack END DESC,
                CASE WHEN ? = 'defense' THEN defense END DESC
        """
        )
        args.addAll(listOf(sortColumn, sortColumn, sortColumn, sortColumn, sortColumn))

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getPokemonsRaw(query)
    }

    @Query("SELECT * FROM pokemons WHERE id = :id")
    abstract fun getPokemonById(id: Int): Flow<PokemonEntity?>

    @Query("DELETE FROM pokemons")
    abstract suspend fun clearAllPokemons()
}