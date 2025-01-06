// Este es el paquete donde está mi pantalla principal para usuarios anónimos
package com.example.myapplication.ui.theme.screens.Quejas

// Importo todas las librerías necesarias para la interfaz
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R

// Pantalla principal para usuarios anónimos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAnonimaScreen(navController: NavHostController) {
    // Defino los colores según el tema (oscuro/claro)
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFF2A2A2A)
    val menuItemColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFF333333)
    val textColor = Color.White
    val accentColor = Color(0xFF1E3A8A)  // Azul oscuro para acentos

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo con efecto blur
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp),
            contentScale = ContentScale.Crop
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

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera con logo y título en la misma línea
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Título principal
                Text(
                    text = "Ayuda a Mejorar\ntu Comunidad",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.ayudacomunidad),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo con fondo semitransparente
            Box(
                modifier = Modifier
                    .background(
                        color = accentColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Selecciona el tipo de problema que deseas reportar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para ver seguimiento de quejas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { navController.navigate("seguimiento_quejas_anonimas") },
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = accentColor.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ver Seguimiento de Quejas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de servicios disponibles
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Lista de opciones con sus iconos correspondientes
                val options = listOf(
                    Pair("Alumbrado", R.drawable.alumbrado),
                    Pair("Alcantarillado", R.drawable.alcantarillado),
                    Pair("Áreas Verdes", R.drawable.areas_verdes),
                    Pair("Baches", R.drawable.baches),
                    Pair("Banquetas", R.drawable.banquetas)
                )

                items(options) { option ->
                    ServiceListItemAnonimo(
                        title = option.first,
                        iconRes = option.second,
                        onClick = {
                            navController.navigate("quejas_anonimas/${option.first}")
                        }
                    )
                }
            }
        }
    }
}

// Componente para mostrar cada servicio en la lista
@Composable
fun ServiceListItemAnonimo(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    // Colores adaptados según el tema
    val isDarkTheme = isSystemInDarkTheme()
    val cardBackgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.7f)  // Fondo claro en tema oscuro
    } else {
        Color(0xFF2A2A2A).copy(alpha = 0.9f)  // Fondo oscuro en tema claro
    }
    val titleColor = if (isDarkTheme) {
        Color(0xFF1E3A8A)  // Azul oscuro en tema oscuro
    } else {
        Color.White  // Blanco en tema claro
    }

    // Tarjeta del servicio
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icono del servicio con fondo circular
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF1E3A8A).copy(alpha = 0.1f), CircleShape)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Título del servicio
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}