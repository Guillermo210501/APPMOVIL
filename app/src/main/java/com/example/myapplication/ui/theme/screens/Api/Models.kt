package com.example.myapplication.ui.theme.screens.Api

// Modelos de datos para la respuesta JSON
data class ApiResponse(
    val nombreEquipo: String,
    val datosTablas: DatosTablas
)

data class DatosTablas(
    val comedatos_ayuda_mejorar_comunidad: List<ServiceItem>
)

data class ServiceItem(
    val id: Int,
    val servicios: String,
    val direccion: String,
    val encargado: String,
    val contacto: String
)
