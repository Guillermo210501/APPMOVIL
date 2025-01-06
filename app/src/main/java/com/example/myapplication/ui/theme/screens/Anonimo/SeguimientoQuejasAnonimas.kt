// Este es el paquete donde está mi pantalla de seguimiento de quejas anónimas
package com.example.myapplication.ui.theme.screens.Anonimo

// Importo todas las librerías necesarias para la interfaz
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.viewmodel.QuejaViewModel

// Esta es la pantalla de seguimiento de quejas anónimas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoQuejasAnonimas(
    navController: NavHostController
) {
    // Inicializo el ViewModel y obtengo la lista de quejas
    val viewModel: QuejaViewModel = hiltViewModel()
    val quejas by viewModel.quejas.collectAsState()

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo con efecto blur
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp)
        )

        // Capa oscura sobre la imagen para mejorar legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        // Estructura principal de la pantalla
        Scaffold(
            // Barra superior con título y botón de regresar
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Seguimiento de Quejas",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.ayudacomunidad),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(vertical = 16.dp)
                )

                // Título principal
                Text(
                    text = "Ayuda a Mejorar tu Comunidad",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Si no hay quejas, muestro un mensaje
                if (quejas.isEmpty()) {
                    Text(
                        text = "No hay quejas anónimas registradas",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Si hay quejas, muestro el título y la lista
                    Text(
                        text = "Quejas Registradas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Lista scrolleable de quejas
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(quejas) { queja ->
                            // Tarjeta para cada queja
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Tipo de queja
                                    Text(
                                        text = "Tipo: ${queja.tipo}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Descripción de la queja
                                    Text(
                                        text = "Descripción: ${queja.descripcion}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.White
                                        )
                                    )

                                    // Ubicación de la queja
                                    Text(
                                        text = "Ubicación: ${queja.calle}, ${queja.colonia}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    )

                                    // Cruzamientos
                                    Text(
                                        text = "Cruzamientos: ${queja.cruzamientos}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    )

                                    // Tiempo de espera
                                    Text(
                                        text = "Tiempo de espera: ${queja.tiempoEspera}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}