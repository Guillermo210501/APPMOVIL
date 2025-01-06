package com.example.myapplication.ui.theme.screens.Admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosAdminScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("usuarios").get().await()
            users = snapshot.documents.mapNotNull { it.data?.plus("id" to it.id) }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.chetumal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 3.dp),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Gestionar Usuarios",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

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
                    text = "Lista de usuarios registrados",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )
                errorMessage != null -> Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                users.isEmpty() -> Text(
                    text = "No hay usuarios registrados.",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(users) { user ->
                        UserCard(
                            user = user,
                            onDelete = { userId ->
                                db.collection("usuarios").document(userId).delete()
                                users = users.filterNot { it["id"] == userId }
                            },
                            onEdit = { selected ->
                                selectedUser = selected
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onSave = { updatedUser ->
                val userId = updatedUser["id"].toString()
                db.collection("usuarios").document(userId).update(updatedUser)
                users = users.map { if (it["id"] == userId) updatedUser else it }
                showEditDialog = false
                selectedUser = null
            },
            onDismiss = {
                showEditDialog = false
                selectedUser = null
            }
        )
    }
}

@Composable
fun UserCard(
    user: Map<String, Any>,
    onDelete: (String) -> Unit,
    onEdit: (Map<String, Any>) -> Unit
) {
    val nombre = user["nombre"]?.toString() ?: "Sin nombre"
    val correo = user["correoElectronico"]?.toString() ?: "null"
    val telefono = user["numeroTelefonico"]?.toString() ?: "No disponible"
    val userId = user["id"]?.toString() ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Usuario: $nombre",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Correo: $correo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF333333)
                )
            )
            Text(
                text = "Teléfono: $telefono",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF333333)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEdit(user) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF4CAF50)
                    )
                }
                IconButton(
                    onClick = { onDelete(userId) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: Map<String, Any>,
    onSave: (Map<String, Any>) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(user["nombre"]?.toString() ?: "") }
    var correo by remember { mutableStateOf(user["correoElectronico"]?.toString() ?: "") }
    var telefono by remember { mutableStateOf(user["numeroTelefonico"]?.toString() ?: "") }
    val isDarkTheme = isSystemInDarkTheme()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF1E3A8A),
        unfocusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
        focusedLabelColor = Color(0xFF1E3A8A),
        unfocusedLabelColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f),
        focusedTextColor = if (isDarkTheme) Color.White else Color.Black,
        unfocusedTextColor = if (isDarkTheme) Color.White else Color.Black,
        cursorColor = if (isDarkTheme) Color.White else Color(0xFF1E3A8A)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Editar Usuario",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = if (isDarkTheme) Color.White else Color(0xFF000000),
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancelar",
                            color = if (isDarkTheme) Color.White else Color(0xFF1E3A8A)
                        )
                    }
                    Button(
                        onClick = {
                            val updatedUser = user.toMutableMap().apply {
                                put("nombre", nombre)
                                put("correoElectronico", correo)
                                put("numeroTelefonico", telefono)
                            }
                            onSave(updatedUser)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A)
                        )
                    ) {
                        Text(
                            text = "Guardar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}