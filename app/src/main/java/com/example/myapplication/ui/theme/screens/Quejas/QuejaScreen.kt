package com.example.myapplication.ui.theme.screens.Quejas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuejaScreen(tipo: String, navController: NavHostController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var cruzamientos by remember { mutableStateOf("") }
    var motivoQueja by remember { mutableStateOf("") }
    var tiempoProblema by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Colores para campos editables
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF000000),
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFF000000)
    )

    // Colores para campos de solo lectura
    val readOnlyTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF000000),
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFF000000),
        disabledTextColor = Color.Black,
        disabledBorderColor = Color.Gray,
        disabledLabelColor = Color.Gray,
        disabledContainerColor = Color(0xFFF5F5F5)
    )

    // Cargar datos del usuario
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                // Log del UID para verificación
                Log.d("QuejaScreen", "Intentando cargar datos para UID: ${user.uid}")

                db.collection("usuarios").document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val data = document.data
                            nombre = data?.get("nombre")?.toString() ?: ""
                            apellidoPaterno = data?.get("apellidoPaterno")?.toString() ?: ""
                            apellidoMaterno = data?.get("apellidoMaterno")?.toString() ?: ""
                            correo = data?.get("correoElectronico")?.toString() ?: ""

                            // Log para verificar los datos cargados
                            Log.d("QuejaScreen", "Datos cargados exitosamente:")
                            Log.d("QuejaScreen", "Nombre: $nombre")
                            Log.d("QuejaScreen", "Apellido Paterno: $apellidoPaterno")
                            Log.d("QuejaScreen", "Apellido Materno: $apellidoMaterno")
                            Log.d("QuejaScreen", "Correo: $correo")
                        } else {
                            Log.e("QuejaScreen", "No se encontró el documento del usuario")
                            Toast.makeText(
                                context,
                                "Error: No se encontraron los datos del usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("QuejaScreen", "Error al cargar datos: ${e.message}")
                        Toast.makeText(
                            context,
                            "Error al cargar los datos: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Log.e("QuejaScreen", "Error general: ${e.message}")
                Toast.makeText(
                    context,
                    "Error general al cargar datos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: run {
            Log.e("QuejaScreen", "No hay usuario autenticado")
            Toast.makeText(
                context,
                "Error: No hay usuario autenticado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = "Fondo Chetumal",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp)
        )

        // Capa oscura sobre la imagen
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón regresar
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del tipo de queja
            val imageResource = when (tipo.lowercase()) {
                "alumbrado" -> R.drawable.alumbrado
                "alcantarillado" -> R.drawable.alcantarillado
                "áreas verdes" -> R.drawable.areas_verdes
                "baches" -> R.drawable.baches
                "banquetas" -> R.drawable.banquetas
                else -> R.drawable.ayudacomunidad
            }

            Image(
                painter = painterResource(id = imageResource),
                contentDescription = tipo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta del título
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Registrar Queja - $tipo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tarjeta de datos personales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Datos Personales",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Campos de solo lectura
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {},
                        label = { Text("Nombre") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = readOnlyTextFieldColors
                    )

                    OutlinedTextField(
                        value = apellidoPaterno,
                        onValueChange = {},
                        label = { Text("Apellido Paterno") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = readOnlyTextFieldColors
                    )

                    OutlinedTextField(
                        value = apellidoMaterno,
                        onValueChange = {},
                        label = { Text("Apellido Materno") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = readOnlyTextFieldColors
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {},
                        label = { Text("Correo Electrónico") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = readOnlyTextFieldColors
                    )
                }
            }

            // Tarjeta de detalles de la queja
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Detalles de la Queja",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Campos editables
                    OutlinedTextField(
                        value = colonia,
                        onValueChange = { colonia = it },
                        label = { Text("Colonia") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = calle,
                        onValueChange = { calle = it },
                        label = { Text("Calle") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = cruzamientos,
                        onValueChange = { cruzamientos = it },
                        label = { Text("Cruzamientos") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = tiempoProblema,
                        onValueChange = { tiempoProblema = it },
                        label = { Text("Tiempo del Problema") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = motivoQueja,
                        onValueChange = { motivoQueja = it },
                        label = { Text("Descripción del problema") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        colors = textFieldColors
                    )
                }
            }

            // Botón enviar queja
            Button(
                onClick = {
                    if (colonia.isBlank() || calle.isBlank() || cruzamientos.isBlank() ||
                        tiempoProblema.isBlank() || motivoQueja.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Por favor complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isLoading = true
                    val queja = hashMapOf(
                        "nombre" to "$nombre $apellidoPaterno $apellidoMaterno",
                        "correo" to correo,
                        "colonia" to colonia,
                        "calle" to calle,
                        "cruzamientos" to cruzamientos,
                        "tiempoProblema" to tiempoProblema,
                        "motivoQueja" to motivoQueja,
                        "estado" to "Pendiente",
                        "tipo" to tipo,
                        "fechaCreacion" to com.google.firebase.Timestamp.now()
                    )

                    db.collection("quejas")
                        .document(tipo)
                        .collection("quejasList")
                        .add(queja)
                        .addOnSuccessListener {
                            isLoading = false
                            Log.d("QuejaScreen", "Queja enviada exitosamente")
                            scope.launch {
                                snackbarHostState.showSnackbar("Queja enviada con éxito")
                            }
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Log.e("QuejaScreen", "Error al enviar queja: ${e.message}")
                            scope.launch {
                                snackbarHostState.showSnackbar("Error al enviar la queja: ${e.message}")
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000000)
                ),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
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

        // Mostrar SnackBar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}