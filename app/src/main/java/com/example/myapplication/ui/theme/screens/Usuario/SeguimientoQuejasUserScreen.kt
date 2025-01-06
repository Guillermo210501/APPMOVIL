package com.example.myapplication.ui.theme.screens.Usuario

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoQuejasUserScreen(navController: NavHostController, auth: FirebaseAuth) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var quejasList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Cargar quejas cuando se inicia la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val userEmail = currentUser?.email
            if (userEmail.isNullOrBlank()) {
                errorMessage = "No se pudo recuperar el correo del usuario."
                isLoading = false
                return@LaunchedEffect
            }
            val categorias = listOf("Alumbrado", "Alcantarillado", "Áreas Verdes", "Baches", "Banquetas")

            // Lista mutable para almacenar todas las quejas
            val allQuejasList = mutableListOf<Map<String, Any>>()
            // Contador para controlar cuándo se han completado todas las consultas
            var completedQueries = 0

            categorias.forEach { categoria ->
                db.collection("quejas")
                    .document(categoria)
                    .collection("quejasList")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        // Añadir cada documento a la lista
                        querySnapshot.documents.forEach { document ->
                            val quejaData = document.data
                            if (quejaData != null) {
                                // Añadir el ID del documento y la categoría al mapa de datos
                                allQuejasList.add(quejaData + mapOf(
                                    "id" to document.id,
                                    "categoria" to categoria
                                ))
                            }
                        }

                        completedQueries++

                        // Cuando se han completado todas las consultas
                        if (completedQueries == categorias.size) {
                            quejasList = allQuejasList
                            isLoading = false
                        }
                    }
                    .addOnFailureListener { e ->
                        completedQueries++
                        errorMessage = "Error al cargar las quejas: ${e.message}"
                        if (completedQueries == categorias.size) {
                            isLoading = false
                        }
                    }
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
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
                            "Seguimiento de Quejas",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
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

                Spacer(modifier = Modifier.height(16.dp))

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
                    quejasList.isEmpty() -> {
                        Text(
                            text = "No tienes quejas registradas",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            text = "Tus Quejas Registradas",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(quejasList) { queja ->
                                QuejaCard(
                                    queja = queja,
                                    onDownloadClick = {
                                        Toast.makeText(context, "Generando PDF...", Toast.LENGTH_SHORT).show()
                                        generateAndSavePDF(context, queja)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuejaCard(queja: Map<String, Any>, onDownloadClick: () -> Unit) {
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
                text = "Tipo: ${queja["tipo"] ?: "N/A"}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Motivo: ${queja["motivoQueja"] ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                )
            )
            Text(
                text = "Ubicación: ${queja["calle"] ?: "N/A"}, ${queja["colonia"] ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "Estado: ${queja["estado"] ?: "Pendiente"}",
                style = MaterialTheme.typography.bodyMedium,
                color = when(queja["estado"]) {
                    "Solucionado" -> Color.Green
                    "En reparación" -> Color(0xFF93C5FD)
                    else -> Color.White.copy(alpha = 0.7f)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDownloadClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Descargar PDF",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

private fun generateAndSavePDF(context: Context, queja: Map<String, Any>) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        }

        // Título
        paint.textSize = 20f
        canvas.drawText("Reporte de Queja", 50f, 50f, paint)
        paint.textSize = 12f

        // Contenido
        var y = 100f
        canvas.drawText("Tipo: ${queja["tipo"] ?: "N/A"}", 50f, y, paint)
        y += 20f
        canvas.drawText("Motivo: ${queja["motivoQueja"] ?: "N/A"}", 50f, y, paint)
        y += 20f
        canvas.drawText("Calle: ${queja["calle"] ?: "N/A"}", 50f, y, paint)
        y += 20f
        canvas.drawText("Colonia: ${queja["colonia"] ?: "N/A"}", 50f, y, paint)
        y += 20f
        canvas.drawText("Estado: ${queja["estado"] ?: "Pendiente"}", 50f, y, paint)

        pdfDocument.finishPage(page)

        // Guardar el PDF
        val fileName = "Queja_${System.currentTimeMillis()}.pdf"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
        } else {
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(filePath, fileName)
            pdfDocument.writeTo(java.io.FileOutputStream(file))
        }

        pdfDocument.close()
        showNotification(context, fileName)
        Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}

private fun showNotification(context: Context, fileName: String) {
    val channelId = "pdf_download_channel"
    val notificationId = 1001

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Descargas de PDF",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones de PDFs descargados"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("PDF Descargado")
        .setContentText("Se ha guardado el archivo: $fileName")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}