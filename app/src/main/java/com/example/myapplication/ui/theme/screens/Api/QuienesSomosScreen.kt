package com.example.myapplication.ui.theme.screens.Api

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
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.data.ApiClient
import com.example.myapplication.ui.theme.screens.Api.ServiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienesSomosScreen(navController: NavHostController) {
    var services by remember { mutableStateOf<List<ServiceItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI0IiwianRpIjoiNWI1MTIwOTg0YThhNGU5NjM2YWE2NTMxMTI0ZGFmYzYzOWI4ZDgzYmY2YzZmZmNmNzU2MTQ2MjgwMmMxMDQ4NDE1NjQxYmQ4ZWMwZTRmNDYiLCJpYXQiOjE3MzQ0ODA4MDAuMzc5NDEsIm5iZiI6MTczNDQ4MDgwMC4zNzk0MTgsImV4cCI6MTc2NjAxNjgwMC4xMzAyNiwic3ViIjoiNTAiLCJzY29wZXMiOltdfQ.NscXE1nRCcWHJPjsF2y-OAn-Sw7lJWLHc2Fuq9mAu71r0gPp-AWZUm7TCO_DASbJEjLvaC1unRBNIzkvGX4G_mnLkO4CBW2IXIoOfNTRuzsZ_SxwNoalfgrLc0XDUeyIFJ8soFaUZhLz_tESA0vXazwqO5pw6KmcpmOOdq6bXt5Dm2q2-CZ1TZ-V6tC0_jaFR_0lqv3OkiqMgvQI-QivV-beGyn7IwbsLL2OpOONyzu1I1KDt-1p7kfRIcegHDVgDv3kxib_Ye7R2iH2gIcnLa7x4Oher0hibgaMKjhwpA31kKMTmaGLjSBX7e8psH5GdzjVW3-KmcVnCeshBleB1K0ibTKhv-piVWb4dppx9_4K7kF9CIgTRKY0OswqImZ5Fo18fgVOQunTZJEgwITVKCEL6eFYJqJep1PYvJgsRVmFKIubJEAeo4pCQwUUgwqFICELFngSi6eTXNFFPFo0rRbnoFolxx8dGzTIJqPSsWU-tD8xMqF4aClwx7L0I-xV9lG04gRZKzN70tPe-eA475ITBVddW-785cTbl9XebNtxSAsSJHp9cBL66NiCz74kBBvW9AwEhQnGy09a1s6hErM4AI5iQCNUB-nSSfEK4hVcWB-catXYdRWFNfEmpsiXKEBR930AW5spr9aPmKLRpAT6W1-cSa6ihDunQ0TRFA"
    val requestBody = mapOf("email" to "l19390075@chetumal.tecnm.mx", "password" to "ATC_2024")

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getServices(token, requestBody)
            services = response.datosTablas.comedatos_ayuda_mejorar_comunidad
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error al cargar los servicios: ${e.message}"
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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

        // Capa de oscurecimiento sobre la imagen
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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Quiénes Somos",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ayudacomunidad),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(vertical = 16.dp)
                )

                Text(
                    text = "Ayuda a Mejorar tu Comunidad",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    services.isEmpty() -> {
                        Text(
                            text = "No hay información disponible",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(services) { service ->
                                ServiceCard(service)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            Text(
                text = service.servicios,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dirección: ${service.direccion}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                )
            )
            Text(
                text = "Encargado: ${service.encargado}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "Contacto: ${service.contacto}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    }
}