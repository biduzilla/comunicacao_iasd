package com.toddy.comunicacaodemandas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.toddy.comunicacaodemandas.database.dao.AnuncioDao
import com.toddy.comunicacaodemandas.modelo.Anuncio

@Database(
    version = 1,
    entities = [Anuncio::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun anuncioDao(): AnuncioDao

    companion object {
        @Volatile
        private var db: AppDatabase? = null

        fun instancia(context: Context): AppDatabase {
            return db ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "anuncio.db"
            ).build()
        }
    }
}