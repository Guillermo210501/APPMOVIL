// Aquí defino el paquete de mi aplicación donde estará la pantalla de crear cuenta
package com.example.myapplication.ui.theme.screens.Usuario

// Importo todas las librerías necesarias para la interfaz y Firebase
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.*
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager

// Esta función maneja todo el proceso de crear una cuenta nueva en Firebase
fun createAccount(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    nombre: String,
    apellidoPaterno: String,
    apellidoMaterno: String,
    numeroTelefonico: String,
    correoElectronico: String,
    contraseña: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    try {
        // Primero creo el usuario en Authentication con email y contraseña
        auth.createUserWithEmailAndPassword(correoElectronico, contraseña)
            .addOnSuccessListener { authResult ->
                // Si se crea bien, obtengo el ID del usuario
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Creo un mapa con toda la información del usuario
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "apellidoPaterno" to apellidoPaterno,
                        "apellidoMaterno" to apellidoMaterno,
                        "numeroTelefonico" to numeroTelefonico,
                        "correoElectronico" to correoElectronico,
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )

                    // Guardo los datos en Firestore en la colección "usuarios"
                    firestore.collection("usuarios")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error al guardar los datos: ${e.message}")
                        }
                } else {
                    onFailure("Error: No se pudo crear el usuario")
                }
            }
            .addOnFailureListener { e ->
                // Manejo los diferentes tipos de errores que pueden ocurrir
                when (e) {
                    is FirebaseAuthWeakPasswordException ->
                        onFailure("La contraseña debe tener al menos 6 caracteres")
                    is FirebaseAuthInvalidCredentialsException ->
                        onFailure("Correo electrónico inválido")
                    is FirebaseAuthUserCollisionException ->
                        onFailure("Ya existe una cuenta con este correo electrónico")
                    else -> onFailure("Error al crear la cuenta: ${e.message}")
                }
            }
    } catch (e: Exception) {
        onFailure("Error inesperado: ${e.message}")
    }
}

// Este componente es para mostrar mensajes de error en color rojo
@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = Color.Red,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(top = 16.dp)
    )
}

// Esta es la pantalla principal de crear cuenta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(navController: NavHostController, auth: FirebaseAuth) {
    // Variables para guardar los datos del formulario
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var numeroTelefonico by remember { mutableStateOf("") }
    var correoElectronico by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmarContraseña by remember { mutableStateOf("") }

    // Variables para controlar la visibilidad de las contraseñas
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Variables para el estado de carga y errores
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Variables para el scroll y contexto
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val focusManager = LocalFocusManager.current

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo con efecto blur para que se vea más estético
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Hago la imagen más grande para que cubra bien la pantalla
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
                .blur(radius = 2.dp)
        )

        // Capa oscura para mejorar la legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // Columna principal con todos los elementos del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = R.drawable.ayudacomunidad),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            // Título principal
            Text(
                text = "Ayuda a Mejorar tu Comunidad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            // Subtítulo
            Text(
                text = "La comunidad en tus manos, el cambio en tu voz",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de nombre - solo acepta letras
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    // Validación para solo aceptar letras y espacios
                    if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                        nombre = it
                    }
                },
                label = { Text("Nombre", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo de apellido paterno - solo acepta letras
            OutlinedTextField(
                value = apellidoPaterno,
                onValueChange = {
                    if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                        apellidoPaterno = it
                    }
                },
                label = { Text("Apellido Paterno", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo de apellido materno - solo acepta letras
            OutlinedTextField(
                value = apellidoMaterno,
                onValueChange = {
                    if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                        apellidoMaterno = it
                    }
                },
                label = { Text("Apellido Materno", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo de número telefónico - solo acepta números y máximo 10 dígitos
            OutlinedTextField(
                value = numeroTelefonico,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        numeroTelefonico = it
                    }
                },
                label = { Text("Número Telefónico", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo de correo electrónico
            OutlinedTextField(
                value = correoElectronico,
                onValueChange = { correoElectronico = it },
                label = { Text("Correo Electrónico", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            // Campo de contraseña con opción de mostrar/ocultar
            OutlinedTextField(
                value = contraseña,
                onValueChange = { contraseña = it },
                label = { Text("Contraseña", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                // Esta parte hace que se muestre como contraseña (con puntos)
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                // Botón para mostrar/ocultar la contraseña
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = Color.White
                        )
                    }
                },
                // Colores personalizados para que se vea bien sobre el fondo oscuro
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo para confirmar contraseña
            OutlinedTextField(
                value = confirmarContraseña,
                onValueChange = { confirmarContraseña = it },
                label = { Text("Confirmar Contraseña", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                // Al presionar "Done" en el teclado, se cierra el teclado
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = Color.White
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Botón de registro con estado de carga
            Button(
                onClick = {
                    // Verifico que todos los campos estén llenos antes de continuar
                    if (nombre.isBlank() || apellidoPaterno.isBlank() || apellidoMaterno.isBlank() ||
                        numeroTelefonico.isBlank() || correoElectronico.isBlank() || contraseña.isBlank()
                    ) {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Activo el indicador de carga
                    isLoading = true
                    createAccount(
                        auth = auth,
                        firestore = firestore,
                        nombre = nombre,
                        apellidoPaterno = apellidoPaterno,
                        apellidoMaterno = apellidoMaterno,
                        numeroTelefonico = numeroTelefonico,
                        correoElectronico = correoElectronico,
                        contraseña = contraseña,
                        onSuccess = {
                            // Si todo sale bien, desactivo la carga y navego al login
                            isLoading = false
                            Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("crear_cuenta") { inclusive = true }
                            }
                        },
                        onFailure = { error ->
                            // Si hay error, lo muestro y desactivo la carga
                            isLoading = false
                            showError = true
                            errorMessage = error
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                // Diseño del botón - uso colores inversos para que destaque
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = !isLoading
            ) {
                // Muestro un indicador de carga o el texto según el estado
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Crear Cuenta")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir al login si ya tiene cuenta
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    "¿Ya tienes cuenta? Inicia sesión",
                    color = Color.White
                )
            }

            // Muestro mensajes de error si hay alguno
            if (showError) {
                ErrorMessage(errorMessage)
            }
        }
    }
}