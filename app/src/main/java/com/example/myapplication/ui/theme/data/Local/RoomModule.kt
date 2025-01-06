package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.QuejaDao
import com.example.myapplication.data.repository.QuejaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    // Migración de la versión 1 a la 2
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Si necesitas realizar cambios específicos en la base de datos
            // puedes agregar sentencias SQL aquí
            // Por ejemplo:
            // database.execSQL("ALTER TABLE quejas_anonimas ADD COLUMN nuevo_campo TEXT")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "queja_database"
        )
            .fallbackToDestructiveMigration() // Esto eliminará y recreará la base de datos si hay cambios
            // Si prefieres mantener los datos, comenta la línea anterior y descomenta la siguiente:
            // .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideQuejaDao(database: AppDatabase): QuejaDao {
        return database.quejaDao()
    }

    @Provides
    @Singleton
    fun provideQuejaRepository(quejaDao: QuejaDao): QuejaRepository {
        return QuejaRepository(quejaDao)
    }
}