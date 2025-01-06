import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViewQuejasScreen(servicio: String, navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var quejasList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efecto para cargar las quejas
    LaunchedEffect(servicio) {
        isLoading = true
        errorMessage = null
        try {
            val snapshot = db.collection("quejas")
                .document(servicio)
                .collection("quejasList")
                .get()
                .await()

            quejasList = if (snapshot.isEmpty) {
                emptyList()
            } else {
                snapshot.documents.mapIndexed { index, document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                        put("index", index)
                    } ?: mutableMapOf()
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar las quejas: ${e.message}"
            quejasList = emptyList()
        } finally {
            isLoading = false
        }
    }

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
                            text = "Administrar Quejas: $servicio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Logo y título
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ayudacomunidad),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Ayuda a Mejorar\ntu Comunidad",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Start
                    )
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    quejasList.isEmpty() -> {
                        Text(
                            text = "No hay quejas registradas.",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quejasList, key = { it["id"].toString() }) { queja ->
                                QuejaCard(
                                    queja = queja,
                                    servicio = servicio,
                                    db = db,
                                    snackbarHostState = snackbarHostState
                                ) { id, nuevoEstado ->
                                    quejasList = quejasList.map { item ->
                                        if (item["id"] == id) {
                                            item.toMutableMap().apply {
                                                this["estado"] = nuevoEstado
                                            }
                                        } else item
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuejaCard(
    queja: Map<String, Any>,
    servicio: String,
    db: FirebaseFirestore,
    snackbarHostState: SnackbarHostState,
    onEstadoCambiado: (String, String) -> Unit
) {
    val id = queja["id"]?.toString() ?: ""
    val nombre = queja["nombre"]?.toString() ?: "Sin nombre"
    val colonia = queja["colonia"]?.toString() ?: "Sin colonia"
    val calle = queja["calle"]?.toString() ?: "Sin calle"
    val cruzamientos = queja["cruzamientos"]?.toString() ?: "Sin cruzamientos"
    val motivo = queja["motivoQueja"]?.toString() ?: "Sin motivo"
    val tiempo = queja["tiempoProblema"]?.toString() ?: "Sin tiempo"
    val estadoActual = queja["estado"]?.toString() ?: "Pendiente"

    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nombre: $nombre",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Colonia: $colonia", color = Color.White)
            Text("Calle: $calle", color = Color.White)
            Text("Cruzamientos: $cruzamientos", color = Color.White)
            Text("Motivo: $motivo", color = Color.White)
            Text("Tiempo del problema: $tiempo", color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estado: $estadoActual",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = when(estadoActual) {
                        "Solucionado" -> Color.Green
                        "En reparación" -> Color(0xFF93C5FD)
                        "Leído" -> Color(0xFF60A5FA)
                        else -> Color.White.copy(alpha = 0.7f)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    db.collection("quejas")
                                        .document(servicio)
                                        .collection("quejasList")
                                        .document(id)
                                        .update("estado", "Leído")
                                        .await()

                                    onEstadoCambiado(id, "Leído")
                                    snackbarHostState.showSnackbar("Estado actualizado a: Leído")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                } finally {
                                    isUpdating = false
                                }
                            }
                        }
                    },
                    enabled = estadoActual == "Pendiente" && !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Leído",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Leído")
                }

                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    db.collection("quejas")
                                        .document(servicio)
                                        .collection("quejasList")
                                        .document(id)
                                        .update("estado", "En reparación")
                                        .await()

                                    onEstadoCambiado(id, "En reparación")
                                    snackbarHostState.showSnackbar("Estado actualizado a: En reparación")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                } finally {
                                    isUpdating = false
                                }
                            }
                        }
                    },
                    enabled = estadoActual == "Leído" && !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "En reparación",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("En reparación")
                }

                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    db.collection("quejas")
                                        .document(servicio)
                                        .collection("quejasList")
                                        .document(id)
                                        .update("estado", "Solucionado")
                                        .await()

                                    onEstadoCambiado(id, "Solucionado")
                                    snackbarHostState.showSnackbar("Estado actualizado a: Solucionado")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                } finally {
                                    isUpdating = false
                                }
                            }
                        }
                    },
                    enabled = estadoActual == "En reparación" && !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43A047),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Solucionado",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Solucionado")
                }
            }
        }
    }
}