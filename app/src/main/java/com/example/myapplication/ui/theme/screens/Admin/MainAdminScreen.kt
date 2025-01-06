// Importo las librerías necesarias para mi pantalla principal de administrador
package com.example.myapplication.ui.theme.screens.Admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAdminScreen(navController: NavHostController, auth: FirebaseAuth) {
    // Inicializo las variables para el menú lateral y el tema
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Creo el menú lateral con navegación
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Configuro el diseño del menú lateral con un degradado según el tema
            ModalDrawerSheet(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    Color(0xFF1E3A8A),  // Azul oscuro para tema oscuro
                                    Color(0xFF2563EB)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFFFFF),  // Blanco para tema claro
                                    Color(0xFFF5F5F5)
                                )
                            }
                        )
                    )
                    .fillMaxHeight()
            ) {
                // Estructura del contenido del menú
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Título del menú administrativo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Text(
                                text = "Menú Administrador",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1E3A8A)
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Línea divisora con color según el tema
                        Divider(
                            color = if (isDarkTheme)
                                Color.White.copy(alpha = 0.2f)
                            else
                                Color(0xFF1E3A8A).copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Opciones del menú con sus iconos y funciones
                        DrawerMenuItemAdmin(
                            label = "Gestionar Usuarios",
                            icon = Icons.Default.Person,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("gestionar_usuarios")
                                }
                            }
                        )

                        DrawerMenuItemAdmin(
                            label = "Reportar queja",
                            icon = Icons.Default.Create,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("quejas_admin")
                                }
                            }
                        )

                        // Botón para cerrar sesión y volver a la pantalla de inicio
                        DrawerMenuItemAdmin(
                            label = "Cerrar sesión",
                            icon = Icons.Default.ExitToApp,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    auth.signOut()
                                    navController.navigate("inicio") {
                                        popUpTo("admin") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // Copyright en la parte inferior del menú
                    Text(
                        text = "© 2024 AyudaComunidad",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDarkTheme)
                                Color.White.copy(alpha = 0.7f)
                            else
                                Color(0xFF1E3A8A).copy(alpha = 0.7f),
                            fontWeight = FontWeight.Light
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) {
        // Contenido principal de la pantalla
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de fondo de Chetumal con efecto blur
            Image(
                painter = painterResource(id = R.drawable.chetumal),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 3.dp),
                contentScale = ContentScale.Crop
            )

            // Capa oscura sobre la imagen para mejor contraste
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

            // Columna principal con el contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón para abrir el menú lateral
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.ayudacomunidad),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Título del panel administrativo
                Text(
                    text = "Panel Administrativo",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Caja con instrucciones para el usuario
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF1E3A8A).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Selecciona el servicio que deseas revisar",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Lista de servicios disponibles
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Defino las opciones de servicios con sus iconos
                    val options = listOf(
                        Pair("Alumbrado", R.drawable.alumbrado),
                        Pair("Alcantarillado", R.drawable.alcantarillado),
                        Pair("Áreas Verdes", R.drawable.areas_verdes),
                        Pair("Baches", R.drawable.baches),
                        Pair("Banquetas", R.drawable.banquetas)
                    )

                    // Creo un elemento en la lista para cada servicio
                    items(options) { option ->
                        ServiceListItemAdmin(
                            title = option.first,
                            iconRes = option.second,
                            onClick = {
                                navController.navigate("admin_ver_quejas/${option.first}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Esta función crea las tarjetas para mostrar cada servicio en la lista principal
@Composable
fun ServiceListItemAdmin(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    // Creo una tarjeta semi-transparente y clicable
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),  // Bordes muy redondeados para el diseño
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),  // Sombra suave
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))  // Color blanco semi-transparente
    ) {
        // Organizo el contenido en una fila
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Contenedor circular para el icono del servicio
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF1E3A8A).copy(alpha = 0.1f), CircleShape)  // Fondo azul muy suave
                    .padding(4.dp)
            ) {
                // Icono del servicio
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

            // Nombre del servicio
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF1E3A8A),  // Color azul para el texto
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.weight(1f))  // Espacio flexible

            // Flecha que indica que se puede hacer clic
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $title",
                tint = Color(0xFF1E3A8A)  // Color azul para la flecha
            )
        }
    }
}

// Esta función crea los elementos del menú lateral
@Composable
fun DrawerMenuItemAdmin(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Defino los colores según el tema actual
    val itemBackgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)  // Fondo claro semi-transparente en tema oscuro
    } else {
        Color(0xFFF5F5F5)  // Gris muy claro en tema claro
    }

    val textAndIconColor = if (isDarkTheme) {
        Color.White  // Texto blanco en tema oscuro
    } else {
        Color(0xFF1E3A8A)  // Texto azul en tema claro
    }

    // Creo una tarjeta clicable para el elemento del menú
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = itemBackgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        // Organizo el icono y el texto en una fila
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del elemento del menú
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textAndIconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Texto del elemento del menú
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textAndIconColor,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}