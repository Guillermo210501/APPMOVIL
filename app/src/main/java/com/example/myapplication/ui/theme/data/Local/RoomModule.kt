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

// Este módulo es para configurar la base de datos Room en mi aplicación
// Lo uso para guardar las quejas de manera local en el dispositivo
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    // Aquí defino cómo actualizar la base de datos de la versión 1 a la 2
    // Lo necesito por si después quiero agregar más campos a mis tablas
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Aquí puedo poner los comandos SQL para actualizar la estructura
            // Por ejemplo, si quiero agregar una nueva columna a mi tabla
            // database.execSQL("ALTER TABLE quejas_anonimas ADD COLUMN nuevo_campo TEXT")
        }
    }

    // Esta función crea la base de datos principal
    // La uso para tener un único punto donde se crea la base de datos
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "queja_database" // Este es el nombre que le doy a mi base de datos
        )
            // Esta línea borra todo si hay cambios en la estructura
            // La uso durante desarrollo para no preocuparme por migraciones
            .fallbackToDestructiveMigration()
            // Si quiero mantener los datos cuando actualizo la app, uso esto:
            // .addMigrations(MIGRATION_1_2)
            .build()
    }

    // Esta función proporciona el DAO para acceder a las quejas
    // La necesito para hacer operaciones en la base de datos como guardar o leer quejas
    @Provides
    fun provideQuejaDao(database: AppDatabase): QuejaDao {
        return database.quejaDao()
    }

    // Esta función crea el repositorio para manejar las quejas
    // Lo uso para tener una capa entre la base de datos y la UI
    @Provides
    @Singleton
    fun provideQuejaRepository(quejaDao: QuejaDao): QuejaRepository {
        return QuejaRepository(quejaDao)
    }
}