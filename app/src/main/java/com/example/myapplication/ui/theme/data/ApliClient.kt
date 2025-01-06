package com.example.myapplication.ui.theme.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// Este es mi objeto para configurar la conexión con la API
// Lo uso como un singleton para tener una única instancia en toda la app
object ApiClient {
    // Esta es la URL base de mi API
    // La defino como constante porque no va a cambiar
    private const val BASE_URL = "http://comedatos.qroo.gob.mx/api/"

    // Esta propiedad configura y crea mi servicio API
    // La hago lazy para que solo se cree cuando la necesite por primera vez
    val apiService: ApiService by lazy {
        // Esto es para ver los logs de las llamadas HTTP
        // Lo uso durante desarrollo para debuggear las llamadas a la API
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Aquí configuro mi cliente HTTP
        // Le agrego el interceptor para ver los logs
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Aquí configuro Retrofit con todas las opciones que necesito
        // - baseUrl: para la URL base de mi API
        // - client: para usar mi cliente HTTP personalizado
        // - GsonConverterFactory: para convertir JSON a objetos de Kotlin
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}