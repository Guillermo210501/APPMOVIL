package com.example.myapplication.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.QuejaAnonima
import com.example.myapplication.data.repository.QuejaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuejaViewModel @Inject constructor(
    private val repository: QuejaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuejaUiState>(QuejaUiState.Loading)
    val uiState: StateFlow<QuejaUiState> = _uiState.asStateFlow()

    private val _quejas = MutableStateFlow<List<QuejaAnonima>>(emptyList())
    val quejas: StateFlow<List<QuejaAnonima>> = _quejas.asStateFlow()

    init {
        cargarQuejas()
    }

    private fun cargarQuejas() {
        viewModelScope.launch {
            try {
                repository.getQuejas().collect { quejasList ->
                    _quejas.value = quejasList
                    _uiState.value = QuejaUiState.Success(quejasList)
                }
            } catch (e: Exception) {
                _uiState.value = QuejaUiState.Error("Error al cargar las quejas: ${e.message}")
            }
        }
    }

    fun insertarQueja(queja: QuejaAnonima) {
        viewModelScope.launch {
            try {
                repository.insertQueja(queja)
                _uiState.value = QuejaUiState.QuejaInsertada
            } catch (e: Exception) {
                _uiState.value = QuejaUiState.Error("Error al insertar la queja: ${e.message}")
            }
        }
    }
}

sealed class QuejaUiState {
    object Loading : QuejaUiState()
    data class Success(val quejas: List<QuejaAnonima>) : QuejaUiState()
    data class Error(val message: String) : QuejaUiState()
    object QuejaInsertada : QuejaUiState()
}