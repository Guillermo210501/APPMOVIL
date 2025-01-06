package com.example.myapplication.data.repository

import com.example.myapplication.data.local.QuejaAnonima
import com.example.myapplication.data.local.QuejaDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuejaRepository @Inject constructor(
    private val quejaDao: QuejaDao
) {
    suspend fun insertQueja(queja: QuejaAnonima) {
        quejaDao.insertQueja(queja)
    }

    fun getQuejas(): Flow<List<QuejaAnonima>> {
        return quejaDao.getQuejas()
    }
}