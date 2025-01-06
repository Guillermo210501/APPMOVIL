// Este es el paquete donde está mi pantalla de quejas anónimas
package com.example.myapplication.ui.theme.screens.Quejas

// Importo todas las librerías necesarias para la interfaz y manejo de datos
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.data.local.QuejaAnonima
import com.example.myapplication.ui.theme.viewmodel.QuejaUiState
import com.example.myapplication.ui.theme.viewmodel.QuejaViewModel

// Pantalla para registrar quejas anónimas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuejasAnonimasScreen(
    tipo: String,  // Tipo de queja (Alumbrado, Baches, etc.)
    navController: NavHostController
) {
    // Inicializo el ViewModel y estados
    val viewModel: QuejaViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Variables para los campos del formulario
    var calle by remember { mutableStateOf("") }
    var cruzamientos by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }
    var tiempoEspera by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Defino colores personalizados para los campos de texto
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFFE53935),  // Rojo para el label enfocado
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFFE53935),        // Rojo para el cursor
        focusedBorderColor = Color(0xFFE53935)  // Rojo para el borde enfocado
    )

    // Efecto que se ejecuta cuando cambia el estado de la UI
    LaunchedEffect(uiState) {
        when (uiState) {
            is QuejaUiState.Error -> {
                showError = true
                errorMessage = (uiState as QuejaUiState.Error).message
            }
            is QuejaUiState.QuejaInsertada -> {
                // Si la queja se insertó correctamente, navego a inicio
                navController.navigate("inicio") {
                    popUpTo("inicio") { inclusive = false }
                }
            }
            else -> {}
        }
    }

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

        // Capa oscura para mejorar legibilidad
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
                .statusBarsPadding()
        ) {
            // Barra superior con título y botón de regresar
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Queja Anónima - $tipo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
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
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Manejo diferentes estados de la UI
            when (uiState) {
                // Mostrar indicador de carga
                is QuejaUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                // Mostrar formulario
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Tarjeta del formulario
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.9f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Título del formulario
                                    Text(
                                        text = "Registrar Nueva Queja",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE53935)
                                        )
                                    )

                                    // Campo para la calle
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

                                    // Campo para los cruzamientos
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

                                    // Campo para la colonia
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

                                    // Campo para el tiempo de espera
                                    OutlinedTextField(
                                        value = tiempoEspera,
                                        onValueChange = { tiempoEspera = it },
                                        label = { Text("Tiempo de Espera") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = textFieldColors,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )

                                    // Campo para la descripción
                                    OutlinedTextField(
                                        value = descripcion,
                                        onValueChange = { descripcion = it },
                                        label = { Text("Descripción del problema") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        colors = textFieldColors,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(
                                            onDone = { focusManager.clearFocus() }
                                        )
                                    )

                                    // Botón para enviar la queja
                                    Button(
                                        onClick = {
                                            // Verifico que todos los campos estén llenos
                                            if (calle.isNotBlank() &&
                                                cruzamientos.isNotBlank() &&
                                                colonia.isNotBlank() &&
                                                tiempoEspera.isNotBlank() &&
                                                descripcion.isNotBlank()
                                            ) {
                                                // Creo y envío la nueva queja
                                                val nuevaQueja = QuejaAnonima(
                                                    tipo = tipo,
                                                    calle = calle,
                                                    cruzamientos = cruzamientos,
                                                    colonia = colonia,
                                                    tiempoEspera = tiempoEspera,
                                                    descripcion = descripcion
                                                )
                                                viewModel.insertarQueja(nuevaQueja)
                                            } else {
                                                // Muestro error si faltan campos
                                                showError = true
                                                errorMessage = "Por favor, completa todos los campos"
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE53935)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Enviar Queja")
                                    }

                                    // Mensaje de error si hay alguno
                                    if (showError) {
                                        Text(
                                            text = errorMessage,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
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
}