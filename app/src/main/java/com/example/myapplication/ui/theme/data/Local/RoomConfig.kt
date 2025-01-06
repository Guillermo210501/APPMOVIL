package com.example.myapplication.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.Date

// Esta clase la uso para convertir las fechas cuando las guardo en la base de datos
// y cuando las leo, porque SQLite no maneja fechas directamente
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// Esta es mi clase principal para las quejas anónimas
// Le puse índices a tipo y colonia para que las búsquedas sean más rápidas
@Entity(
    tableName = "quejas_anonimas",
    indices = [
        Index(value = ["tipo"]),
        Index(value = ["colonia"])
    ]
)
data class QuejaAnonima(
    // El id se genera solo, por eso uso autoGenerate
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Estos son todos los campos que necesito guardar de una queja
    @ColumnInfo(name = "tipo")
    val tipo: String,

    @ColumnInfo(name = "calle")
    val calle: String,

    @ColumnInfo(name = "cruzamientos")
    val cruzamientos: String,

    @ColumnInfo(name = "colonia")
    val colonia: String,

    @ColumnInfo(name = "tiempo_espera")
    val tiempoEspera: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    // La fecha se genera automáticamente cuando se crea la queja
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Date = Date(),

    // Por defecto todas las quejas empiezan como PENDIENTE
    @ColumnInfo(name = "estado", defaultValue = "PENDIENTE")
    val estado: String = "PENDIENTE"
)

// Aquí pongo todos los métodos que necesito para trabajar con las quejas
// Lo hice con Flow para que la UI se actualice automáticamente cuando hay cambios
@Dao
interface QuejaDao {
    // Métodos básicos para agregar, actualizar y borrar quejas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueja(queja: QuejaAnonima)

    // Este lo uso cuando necesito guardar varias quejas a la vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuejas(quejas: List<QuejaAnonima>)

    @Update
    suspend fun updateQueja(queja: QuejaAnonima)

    @Delete
    suspend fun deleteQueja(queja: QuejaAnonima)

    // Estas son mis consultas personalizadas
    // Las ordeno por fecha para ver primero las más recientes
    @Query("SELECT * FROM quejas_anonimas ORDER BY fecha_creacion DESC")
    fun getQuejas(): Flow<List<QuejaAnonima>>

    // Esta la uso para filtrar por tipo de queja (baches, alumbrado, etc)
    @Query("SELECT * FROM quejas_anonimas WHERE tipo = :tipo ORDER BY fecha_creacion DESC")
    fun getQuejasByTipo(tipo: String): Flow<List<QuejaAnonima>>

    // Con esta busco quejas por colonia
    @Query("SELECT * FROM quejas_anonimas WHERE colonia = :colonia ORDER BY fecha_creacion DESC")
    fun getQuejasByColonia(colonia: String): Flow<List<QuejaAnonima>>

    // Para ver el detalle de una queja específica
    @Query("SELECT * FROM quejas_anonimas WHERE id = :id")
    suspend fun getQuejaById(id: Int): QuejaAnonima?

    // Para filtrar por estado (pendiente, en proceso, etc)
    @Query("SELECT * FROM quejas_anonimas WHERE estado = :estado ORDER BY fecha_creacion DESC")
    fun getQuejasByEstado(estado: String): Flow<List<QuejaAnonima>>

    // Por si necesito limpiar toda la base de datos
    @Query("DELETE FROM quejas_anonimas")
    suspend fun deleteAllQuejas()

    // Estas las uso para estadísticas
    @Query("SELECT COUNT(*) FROM quejas_anonimas")
    fun getTotalQuejas(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quejas_anonimas WHERE tipo = :tipo")
    fun getTotalQuejasByTipo(tipo: String): Flow<Int>
}

// Esta es mi base de datos principal
// La versión 2 es porque le hice algunos cambios después de la primera versión
@Database(
    entities = [QuejaAnonima::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quejaDao(): QuejaDao

    companion object {
        private const val DATABASE_NAME = "queja_database"

        // Uso esto para asegurarme de que solo haya una instancia de la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // Por ahora lo dejé así, pero después tengo que hacer migraciones properly
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Aquí podría poner datos iniciales si quisiera
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Esto lo tengo preparado para cuando necesite hacer cambios en la estructura de la base de datos
object DatabaseMigrations {
    // Por ejemplo, si necesito agregar una columna nueva
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Aquí pondría los cambios cuando los necesite
            // database.execSQL("ALTER TABLE quejas_anonimas ADD COLUMN nueva_columna TEXT")
        }
    }
}