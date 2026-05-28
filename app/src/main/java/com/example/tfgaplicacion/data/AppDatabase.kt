package com.example.tfgaplicacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tfgaplicacion.model.Adiccion
import com.example.tfgaplicacion.model.RegistroDiario
import com.example.tfgaplicacion.model.Usuario
import com.example.tfgaplicacion.model.ProgresoUsuario
import com.example.tfgaplicacion.model.Insignia

@Database(
    entities = [Usuario::class, Adiccion::class, RegistroDiario::class, ProgresoUsuario::class, Insignia::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun adiccionDao(): AdiccionDao
    abstract fun registroDiarioDao(): RegistroDiarioDao
    abstract fun progresoDao(): ProgresoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "adicciones_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
