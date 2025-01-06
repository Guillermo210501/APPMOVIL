package com.example.myapplication.ui.theme.data

import com.example.myapplication.ui.theme.screens.Api.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Esta es mi interfaz para hacer llamadas a la API de NucleoDigital
// La uso para definir todos los endpoints que voy a necesitar en mi app
interface ApiService {
    // Esta función hace una petición POST al endpoint "NucleoDigital"
    // La uso para obtener servicios del backend
    @POST("NucleoDigital")
    suspend fun getServices(
        // Aquí paso el token de autorización en el header
        // Lo necesito para autenticarme con el servidor
        @Header("Authorization") token: String,

        // Aquí paso los parámetros que necesita el servidor
        // Uso Map<String, String> para poder enviar diferentes pares de clave-valor
        @Body requestBody: Map<String, String>
    ): ApiResponse // Esta es la respuesta que espero recibir del servidor
}