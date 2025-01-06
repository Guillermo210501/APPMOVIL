// Importo todas las librerías que necesito para el formulario de quejas
package com.example.myapplication.ui.theme.screens.admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Defino una clase para manejar los servicios y sus imágenes
data class ServiceItem(
    val name: String,
    val imageResource: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuejasAdminScreen(navController: NavHostController) {
    // Variables para controlar el estado de la pantalla y el formulario
    var selectedService by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Declaro todas las variables para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var cruzamientos by remember { mutableStateOf("") }
    var tiempoProblema by remember { mutableStateOf("") }
    var motivoQueja by remember { mutableStateOf("") }
    var numTelefonico by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Configuro los colores personalizados para los campos de texto
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,          // Color del texto cuando está seleccionado
        unfocusedTextColor = Color.Black,        // Color del texto normal
        focusedLabelColor = Color(0xFF1E3A8A),   // Color de la etiqueta seleccionada
        unfocusedLabelColor = Color.Gray,        // Color de la etiqueta normal
        cursorColor = Color(0xFF1E3A8A),         // Color del cursor
        focusedBorderColor = Color(0xFF1E3A8A)   // Color del borde cuando está seleccionado
    )

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pongo la imagen de Chetumal de fondo con efecto blur
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp),
            contentScale = ContentScale.Crop
        )

        // Agrego una capa oscura encima de la imagen para mejor contraste
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

        // Columna principal que contiene todo el contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra superior con botón de regreso y título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para regresar o deseleccionar servicio
                IconButton(
                    onClick = {
                        if (selectedService != null) {
                            selectedService = null  // Si hay servicio seleccionado, lo quito
                        } else {
                            navController.navigateUp()  // Si no, regreso a la pantalla anterior
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
                // Título que cambia según si hay servicio seleccionado
                Text(
                    text = selectedService?.let { "Reportar Queja - $it" } ?: "Selecciona el Servicio",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Caja con instrucciones para el usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1E3A8A).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (selectedService == null)
                        "Selecciona el tipo de servicio a reportar"
                    else
                        "Ingresa los datos de la queja",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }


// Agrega un espacio vertical de 24dp
            Spacer(modifier = Modifier.height(24.dp))

            if (selectedService == null) {
                // Muestra la lista de servicios si no se ha seleccionado ninguno
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Define la lista de servicios con sus nombres e imágenes correspondientes
                        val services = listOf(
                            ServiceItem("Alumbrado", R.drawable.alumbrado),
                            ServiceItem("Alcantarillado", R.drawable.alcantarillado),
                            ServiceItem("Áreas Verdes", R.drawable.areas_verdes),
                            ServiceItem("Baches", R.drawable.baches),
                            ServiceItem("Banquetas", R.drawable.banquetas)
                        )

                        // Itera sobre la lista de servicios y crea una tarjeta para cada uno
                        services.forEach { service ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable { selectedService = service.name },
                                shape = RoundedCornerShape(50.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.7f)
                                )
                            ) {
                                // Muestra el nombre y la imagen del servicio en una fila
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Crea un contenedor para la imagen del servicio con un fondo circular
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(
                                                Color(0xFF1E3A8A).copy(alpha = 0.1f),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp)
                                    ) {
                                        // Muestra la imagen del servicio dentro del contenedor circular
                                        Image(
                                            painter = painterResource(id = service.imageResource),
                                            contentDescription = service.name,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // Agrega un espacio horizontal de 16dp entre la imagen y el texto
                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Muestra el nombre del servicio
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF1E3A8A),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            // Agrega un espacio vertical de 8dp entre cada tarjeta de servicio
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            } else {
                // Muestra el formulario de queja si se ha seleccionado un servicio
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Muestra la imagen del servicio seleccionado
                        Image(
                            painter = painterResource(
                                id = when (selectedService?.lowercase()) {
                                    "alumbrado" -> R.drawable.alumbrado
                                    "alcantarillado" -> R.drawable.alcantarillado
                                    "áreas verdes" -> R.drawable.areas_verdes
                                    "baches" -> R.drawable.baches
                                    "banquetas" -> R.drawable.banquetas
                                    else -> R.drawable.ayudacomunidad
                                }
                            ),
                            contentDescription = selectedService,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Agrega un espacio vertical de 16dp
                        Spacer(modifier = Modifier.height(16.dp))

                        // Muestra una tarjeta para ingresar los datos personales
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Título de la sección de datos personales
                                Text(
                                    "Datos Personales",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF1E3A8A),
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Campo de texto para ingresar el nombre
                                OutlinedTextField(
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    label = { Text("Nombre") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar el apellido paterno
                                OutlinedTextField(
                                    value = apellidoPaterno,
                                    onValueChange = { apellidoPaterno = it },
                                    label = { Text("Apellido Paterno") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar el apellido materno
                                OutlinedTextField(
                                    value = apellidoMaterno,
                                    onValueChange = { apellidoMaterno = it },
                                    label = { Text("Apellido Materno") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar el correo electrónico
                                OutlinedTextField(
                                    value = correo,
                                    onValueChange = { correo = it },
                                    label = { Text("Correo Electrónico") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar el número telefónico
                                OutlinedTextField(
                                    value = numTelefonico,
                                    onValueChange = { numTelefonico = it },
                                    label = { Text("Número Telefónico") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                            }
                        }

                        // Agrega un espacio vertical de 16dp
                        Spacer(modifier = Modifier.height(16.dp))

                        // Muestra una tarjeta para ingresar los detalles de la queja
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Título de la sección de detalles de la queja
                                Text(
                                    "Detalles de la Queja",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF1E3A8A),
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Campo de texto para ingresar la colonia
                                OutlinedTextField(
                                    value = colonia,
                                    onValueChange = { colonia = it },
                                    label = { Text("Colonia") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar la calle
                                OutlinedTextField(
                                    value = calle,
                                    onValueChange = { calle = it },
                                    label = { Text("Calle") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar los cruzamientos
                                OutlinedTextField(
                                    value = cruzamientos,
                                    onValueChange = { cruzamientos = it },
                                    label = { Text("Cruzamientos") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto para ingresar el tiempo del problema
                                OutlinedTextField(
                                    value = tiempoProblema,
                                    onValueChange = { tiempoProblema = it },
                                    label = { Text("Tiempo del Problema") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )

                                // Campo de texto de varias líneas para ingresar el motivo de la queja
                                OutlinedTextField(
                                    value = motivoQueja,
                                    onValueChange = { motivoQueja = it },
                                    label = { Text("Motivo de la Queja") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors,
                                    minLines = 3,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = { focusManager.clearFocus() }
                                    )
                                )
                            }
                        }

                        // Agrega un espacio vertical de 16dp
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para enviar la queja
                        Button(
                            onClick = {
                                // Verifica si todos los campos están completos
                                if (nombre.isBlank() || apellidoPaterno.isBlank() || apellidoMaterno.isBlank() ||
                                    correo.isBlank() || colonia.isBlank() || calle.isBlank() ||
                                    cruzamientos.isBlank() || tiempoProblema.isBlank() || motivoQueja.isBlank() ||
                                    numTelefonico.isBlank()
                                ) {
                                    // Muestra un mensaje de error si algún campo está vacío
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Por favor complete todos los campos")
                                    }
                                    return@Button
                                }

                                // Indica que se está enviando la queja
                                isLoading = true
                                // Obtiene una instancia de Firestore
                                val db = FirebaseFirestore.getInstance()

                                // Crea un objeto con los datos de la queja
                                val queja = hashMapOf(
                                    "nombre" to "$nombre $apellidoPaterno $apellidoMaterno",
                                    "correo" to correo,
                                    "numTelefonico" to numTelefonico,
                                    "colonia" to colonia,
                                    "calle" to calle,
                                    "cruzamientos" to cruzamientos,
                                    "tiempoProblema" to tiempoProblema,
                                    "motivoQueja" to motivoQueja,
                                    "estado" to "Pendiente",
                                    "tipo" to selectedService
                                )

                                // Guarda la queja en Firestore según el tipo de servicio seleccionado
                                selectedService?.let { servicio ->
                                    db.collection("quejas")
                                        .document(servicio)
                                        .collection("quejasList")
                                        .add(queja)
                                        .addOnSuccessListener {
                                            // Indica que se ha enviado la queja correctamente
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Queja enviada con éxito",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Limpia los campos después de enviar la queja
                                            nombre = ""
                                            apellidoPaterno = ""
                                            apellidoMaterno = ""
                                            correo = ""
                                            numTelefonico = ""
                                            colonia = ""
                                            calle = ""
                                            cruzamientos = ""
                                            tiempoProblema = ""
                                            motivoQueja = ""
                                            selectedService = null // Regresa a la pantalla de selección de servicio
                                        }
                                        .addOnFailureListener { e ->
                                            // Muestra un mensaje de error si ocurre un problema al enviar la queja
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Error al enviar la queja: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                // Muestra un indicador de carga mientras se envía la queja
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Enviar Queja")
                            }
                        }
                    }
                }
            }
        }

        // Snackbar para mostrar mensajes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}