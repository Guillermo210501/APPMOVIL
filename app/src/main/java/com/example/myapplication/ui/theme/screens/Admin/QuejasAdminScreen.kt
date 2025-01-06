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

data class ServiceItem(
    val name: String,
    val imageResource: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuejasAdminScreen(navController: NavHostController) {
    var selectedService by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados para los campos del formulario
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

    // Colores personalizados para los campos de texto
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF1E3A8A),
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFF1E3A8A),
        focusedBorderColor = Color(0xFF1E3A8A)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo con blur
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp),
            contentScale = ContentScale.Crop
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (selectedService != null) {
                            selectedService = null
                        } else {
                            navController.navigateUp()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
                Text(
                    text = selectedService?.let { "Reportar Queja - $it" } ?: "Selecciona el Servicio",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Subtítulo con fondo
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

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedService == null) {
                // Lista de servicios
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val services = listOf(
                            ServiceItem("Alumbrado", R.drawable.alumbrado),
                            ServiceItem("Alcantarillado", R.drawable.alcantarillado),
                            ServiceItem("Áreas Verdes", R.drawable.areas_verdes),
                            ServiceItem("Baches", R.drawable.baches),
                            ServiceItem("Banquetas", R.drawable.banquetas)
                        )

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
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(
                                                Color(0xFF1E3A8A).copy(alpha = 0.1f),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = service.imageResource),
                                            contentDescription = service.name,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF1E3A8A),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            } else {
                // Formulario de queja
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Imagen del servicio seleccionado
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card para datos personales
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
                                Text(
                                    "Datos Personales",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF1E3A8A),
                                        fontWeight = FontWeight.Bold
                                    )
                                )

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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card para detalles de la queja
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
                                Text(
                                    "Detalles de la Queja",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF1E3A8A),
                                        fontWeight = FontWeight.Bold
                                    )
                                )

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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de enviar queja
                        Button(
                            onClick = {
                                if (nombre.isBlank() || apellidoPaterno.isBlank() || apellidoMaterno.isBlank() ||
                                    correo.isBlank() || colonia.isBlank() || calle.isBlank() ||
                                    cruzamientos.isBlank() || tiempoProblema.isBlank() || motivoQueja.isBlank() ||
                                    numTelefonico.isBlank()
                                ) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Por favor complete todos los campos")
                                    }
                                    return@Button
                                }

                                isLoading = true
                                val db = FirebaseFirestore.getInstance()

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

                                selectedService?.let { servicio ->
                                    db.collection("quejas")
                                        .document(servicio)
                                        .collection("quejasList")
                                        .add(queja)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Queja enviada con éxito",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Limpiar los campos después de enviar
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
                                            selectedService = null  // Regresa a la pantalla de selección de servicio
                                        }
                                        .addOnFailureListener { e ->
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