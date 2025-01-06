// Este es el paquete donde están mis modelos de datos para la API
package com.example.myapplication.ui.theme.screens.Api

// Esta clase representa la estructura principal de la respuesta de la API
// Contiene el nombre del equipo y los datos de las tablas
data class ApiResponse(
    // Nombre del equipo o proyecto
    val nombreEquipo: String,
    // Objeto que contiene la lista de servicios
    val datosTablas: DatosTablas
)

// Esta clase representa la estructura de los datos de las tablas
// Contiene una lista de servicios disponibles
data class DatosTablas(
    // Lista de servicios de ayuda para mejorar la comunidad
    // El nombre del campo coincide con el nombre de la tabla en la API
    val comedatos_ayuda_mejorar_comunidad: List<ServiceItem>
)

// Esta clase representa cada servicio individual
// Contiene toda la información de un servicio específico
data class ServiceItem(
    // Identificador único del servicio
    val id: Int,
    // Nombre o tipo de servicio
    val servicios: String,
    // Dirección donde se encuentra el servicio
    val direccion: String,
    // Nombre del encargado del servicio
    val encargado: String,
    // Información de contacto (teléfono, email, etc.)
    val contacto: String
)