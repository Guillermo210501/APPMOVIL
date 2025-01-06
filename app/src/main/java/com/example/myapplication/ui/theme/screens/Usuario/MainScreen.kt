package com.example.myapplication.ui.theme.screens.Usuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun MainScreen(navController: NavHostController, auth: FirebaseAuth) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    Color(0xFF1E3A8A),
                                    Color(0xFF2563EB)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFF5F5F5)
                                )
                            }
                        )
                    )
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Text(
                                text = "Menú",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1E3A8A)
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        Divider(
                            color = if (isDarkTheme)
                                Color.White.copy(alpha = 0.2f)
                            else
                                Color(0xFF1E3A8A).copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        DrawerMenuItem(
                            label = "Quiénes somos",
                            icon = Icons.Default.Info,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("quienes_somos")
                                }
                            }
                        )

                        DrawerMenuItem(
                            label = "Seguimiento de quejas",
                            icon = Icons.Default.Search,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("seguimiento_quejas")
                                }
                            }
                        )

                        DrawerMenuItem(
                            label = "Cerrar sesión",
                            icon = Icons.Default.ExitToApp,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    auth.signOut()
                                    navController.navigate("inicio") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.chetumal),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

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
                // Menú hamburguesa
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

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ayudacomunidad),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Título
                Text(
                    text = "Ayuda a Mejorar tu Comunidad",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtítulo con fondo
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF1E3A8A).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Selecciona el tipo de problema que deseas reportar",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Lista de servicios
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val options = listOf(
                        Pair("Alumbrado", R.drawable.alumbrado),
                        Pair("Alcantarillado", R.drawable.alcantarillado),
                        Pair("Áreas Verdes", R.drawable.areas_verdes),
                        Pair("Baches", R.drawable.baches),
                        Pair("Banquetas", R.drawable.banquetas)
                    )

                    items(options) { option ->
                        ServiceListItem(
                            title = option.first,
                            iconRes = option.second,
                            onClick = {
                                navController.navigate("quejas/${option.first}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceListItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icono del servicio
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
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold
                )
            )

            // Flecha a la derecha
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $title",
                tint = Color(0xFF1E3A8A)
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Colores adaptados según el tema
    val itemBackgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color(0xFFF5F5F5) // Gris muy claro para modo claro
    }

    val textAndIconColor = if (isDarkTheme) {
        Color.White
    } else {
        Color(0xFF1E3A8A) // Azul oscuro para modo claro
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textAndIconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
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