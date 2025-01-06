package com.example.myapplication.data.repository

import com.example.myapplication.data.local.QuejaAnonima
import com.example.myapplication.data.local.QuejaDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Esta es mi clase repositorio para manejar las quejas anónimas
// La uso para tener un lugar centralizado donde gestionar todas las operaciones de quejas
class QuejaRepository @Inject constructor(
    // Este es mi DAO que necesito para acceder a la base de datos
    // Lo inyecto aquí para no tener que crearlo manualmente
    private val quejaDao: QuejaDao
) {
    // Esta función me sirve para guardar una nueva queja en la base de datos
    // La hago suspend porque es una operación de base de datos y no quiero bloquear el hilo principal
    suspend fun insertQueja(queja: QuejaAnonima) {
        quejaDao.insertQueja(queja)
    }

    // Esta función me devuelve todas las quejas como un Flow
    // Uso Flow para que mi UI se actualice automáticamente cuando hay cambios en la base de datos
    fun getQuejas(): Flow<List<QuejaAnonima>> {
        return quejaDao.getQuejas()
    }
}