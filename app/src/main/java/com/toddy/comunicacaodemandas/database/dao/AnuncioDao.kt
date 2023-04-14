package com.toddy.comunicacaodemandas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.toddy.comunicacaodemandas.modelo.Anuncio
import kotlinx.coroutines.flow.Flow

@Dao
interface AnuncioDao {

    @Insert(onConflict = REPLACE)
    suspend fun salvar(anunncio: Anuncio)

    @Query("SELECT * FROM Anuncio")
    fun buscaTodas(): Flow<List<Anuncio>>

    @Query("SELECT * FROM Anuncio WHERE id = :id")
    fun buscarPorId(id: String): Flow<Anuncio>

    @Query("DELETE FROM Anuncio WHERE id = :id")
    suspend fun remover(id: String)
}