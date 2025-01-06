// Importo las librerías necesarias para crear la interfaz y manejar la base de datos
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
    // Inicio la conexión con Firebase y creo las variables para manejar los estados
    val db = FirebaseFirestore.getInstance()
    var quejasList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Este efecto se ejecuta cuando entro a la pantalla o cambia el servicio
    // Se encarga de cargar las quejas desde Firebase
    LaunchedEffect(servicio) {
        isLoading = true
        errorMessage = null
        try {
            // Busco las quejas en la colección del servicio seleccionado
            val snapshot = db.collection("quejas")
                .document(servicio)
                .collection("quejasList")
                .get()
                .await()

            // Si no hay quejas, devuelvo lista vacía, si hay, las mapeo a una lista
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
            // Si hay error, lo guardo y limpio la lista
            errorMessage = "Error al cargar las quejas: ${e.message}"
            quejasList = emptyList()
        } finally {
            isLoading = false
        }
    }

    // Contenedor principal que ocupa toda la pantalla
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pongo la imagen de Chetumal de fondo con efecto blur para que no distraiga
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp)
        )

        // Agrego una capa oscura encima de la imagen para mejorar la legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),  // Más oscuro arriba
                            Color.Black.copy(alpha = 0.5f)   // Menos oscuro abajo
                        )
                    )
                )
        )

        // Estructura principal con barra superior y contenido
        Scaffold(
            topBar = {
                // Barra superior con título y botón para regresar
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
            // Columna principal que contiene todo el contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Cabecera con logo y título de la aplicación
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

                // Manejo los diferentes estados de la pantalla
                when {
                    // Mientras carga, muestro un indicador circular
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    // Si hay error, lo muestro en rojo
                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    // Si no hay quejas, muestro un mensaje
                    quejasList.isEmpty() -> {
                        Text(
                            text = "No hay quejas registradas.",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    // Si hay quejas, las muestro en una lista scrolleable
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Creo una tarjeta por cada queja
                            items(quejasList, key = { it["id"].toString() }) { queja ->
                                QuejaCard(
                                    queja = queja,
                                    servicio = servicio,
                                    db = db,
                                    snackbarHostState = snackbarHostState
                                ) { id, nuevoEstado ->
                                    // Actualizo la lista cuando cambia el estado de una queja
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
// Esta función crea las tarjetas que muestran cada queja individual
@Composable
fun QuejaCard(
    queja: Map<String, Any>,
    servicio: String,
    db: FirebaseFirestore,
    snackbarHostState: SnackbarHostState,
    onEstadoCambiado: (String, String) -> Unit
) {
    // Extraigo los datos de la queja del mapa, si no existe algún valor, uso un texto por defecto
    val id = queja["id"]?.toString() ?: ""
    val nombre = queja["nombre"]?.toString() ?: "Sin nombre"
    val colonia = queja["colonia"]?.toString() ?: "Sin colonia"
    val calle = queja["calle"]?.toString() ?: "Sin calle"
    val cruzamientos = queja["cruzamientos"]?.toString() ?: "Sin cruzamientos"
    val motivo = queja["motivoQueja"]?.toString() ?: "Sin motivo"
    val tiempo = queja["tiempoProblema"]?.toString() ?: "Sin tiempo"
    val estadoActual = queja["estado"]?.toString() ?: "Pendiente"

    // Variables para controlar el estado de actualización
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Creo una tarjeta semi-transparente para mostrar la información
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
            // Nombre del usuario que hizo la queja
            Text(
                text = "Nombre: $nombre",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Detalles de la ubicación y el problema
            Text("Colonia: $colonia", color = Color.White)
            Text("Calle: $calle", color = Color.White)
            Text("Cruzamientos: $cruzamientos", color = Color.White)
            Text("Motivo: $motivo", color = Color.White)
            Text("Tiempo del problema: $tiempo", color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            // Estado actual con color diferente según su valor
            Text(
                text = "Estado: $estadoActual",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = when(estadoActual) {
                        "Solucionado" -> Color.Green          // Verde para solucionado
                        "En reparación" -> Color(0xFF93C5FD)  // Azul claro para en reparación
                        "Leído" -> Color(0xFF60A5FA)          // Azul más claro para leído
                        else -> Color.White.copy(alpha = 0.7f) // Blanco para pendiente
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fila de botones para cambiar el estado
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón rojo para marcar como "Leído"
                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    // Actualizo el estado en Firebase
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
                    enabled = estadoActual == "Pendiente" && !isUpdating,  // Solo se puede marcar como leído si está pendiente
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),  // Color rojo
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

                // Botón azul para marcar como "En reparación"
                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    // Actualizo el estado en Firebase
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
                    enabled = estadoActual == "Leído" && !isUpdating,  // Solo se puede marcar en reparación si está leído
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),  // Color azul
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

                // Botón verde para marcar como "Solucionado"
                Button(
                    onClick = {
                        if (!isUpdating) {
                            isUpdating = true
                            scope.launch {
                                try {
                                    // Actualizo el estado en Firebase
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
                    enabled = estadoActual == "En reparación" && !isUpdating,  // Solo se puede marcar como solucionado si está en reparación
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43A047),  // Color verde
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