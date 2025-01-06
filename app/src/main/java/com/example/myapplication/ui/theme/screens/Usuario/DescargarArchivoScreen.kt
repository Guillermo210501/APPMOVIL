// Este es el paquete donde está mi pantalla de descargar archivos
package com.example.myapplication.ui.theme.screens.Usuario

// Importo todas las librerías necesarias para manejar archivos, PDF y notificaciones
import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.myapplication.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.IOException

// Esta clase guarda todos los datos del formulario de quejas
data class QuejaFormState(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val colonia: String = "",
    val calle: String = "",
    val cruzamientos: String = "",
    val motivoQueja: String = "",
    val tiempoProblema: String = ""
) {
    // Esta función verifica que todos los campos estén llenos
    fun isValid(): Boolean {
        return nombre.isNotEmpty() &&
                apellidoPaterno.isNotEmpty() &&
                apellidoMaterno.isNotEmpty() &&
                correo.isNotEmpty() &&
                colonia.isNotEmpty() &&
                calle.isNotEmpty() &&
                cruzamientos.isNotEmpty() &&
                motivoQueja.isNotEmpty() &&
                tiempoProblema.isNotEmpty()
    }
}

// Esta función crea un PDF con los datos de la queja
fun saveQuejaAsPDF(
    context: Context,
    tipo: String,
    descripcion: String,
    calle: String,
    colonia: String,
    tiempo: String,
    estado: String
): Boolean {
    // Creo un nuevo documento PDF
    val pdfDocument = PdfDocument()
    // Configuro el tamaño de la página (A4)
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    // Configuro los estilos de texto para el PDF
    val textPaint = Paint().apply {
        textSize = 16f
        color = android.graphics.Color.BLACK
    }
    // Estilo para el título con texto más grande y centrado
    val titlePaint = Paint().apply {
        textSize = 22f
        isFakeBoldText = true
        color = android.graphics.Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    // Dibujo el título en el centro de la página
    canvas.drawText("Reporte de Queja", 297.5f, 50f, titlePaint)

    // Agrego el logo de la aplicación
    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ayudacomunidad)
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 120, 120, true)
    canvas.drawBitmap(scaledBitmap, 450f, 20f, null)

    // Escribo todos los datos de la queja en el PDF
    var y = 100f  // Variable para controlar la posición vertical del texto
    canvas.drawText("Tipo: $tipo", 50f, y, textPaint)
    y += 20f
    canvas.drawText("Descripción: $descripcion", 50f, y, textPaint)
    y += 20f
    canvas.drawText("Calle: $calle", 50f, y, textPaint)
    y += 20f
    canvas.drawText("Colonia: $colonia", 50f, y, textPaint)
    y += 20f
    canvas.drawText("Tiempo del Problema: $tiempo", 50f, y, textPaint)
    y += 20f
    canvas.drawText("Estado: $estado", 50f, y, textPaint)

    pdfDocument.finishPage(page)

    // Intento guardar el PDF en el dispositivo
    return try {
        val fileName = "queja_${System.currentTimeMillis()}.pdf"
        val outputStream: OutputStream?

        // Manejo diferente para Android 10 (Q) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // En Android 10+ uso MediaStore para guardar el archivo
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            // Para versiones anteriores uso el método tradicional
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            outputStream = FileOutputStream(file)
        }

        // Guardo el PDF y muestro una notificación
        outputStream?.use { pdfDocument.writeTo(it) }
        pdfDocument.close()
        showDownloadNotification(context, fileName)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Esta función muestra una notificación cuando se descarga un archivo
fun showDownloadNotification(context: Context, fileName: String) {
    val channelId = "pdf_download_channel"
    val notificationId = 1001

    // Creo un canal de notificación para Android 8.0 (O) o superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Descargas de PDF",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones de archivos descargados"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Configuro cómo se verá la notificación
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ayudacomunidad)
        .setContentTitle("Archivo Descargado")
        .setContentText("Se ha descargado el archivo: $fileName")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // Intento mostrar la notificación
    try {
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

// Esta función crea un archivo de texto con los datos de la queja
fun createQuejaTextFile(context: Context, formState: QuejaFormState, tipo: String): File? {
    // Creo un nombre único para el archivo
    val fileName = "queja_${tipo}_${System.currentTimeMillis()}.txt"

    // Preparo el contenido del archivo con formato
    val fileContent = """
        Queja de tipo: $tipo
        ----------------------------------
        Nombre: ${formState.nombre}
        Apellido Paterno: ${formState.apellidoPaterno}
        Apellido Materno: ${formState.apellidoMaterno}
        Colonia: ${formState.colonia}
        Calle: ${formState.calle}
        Cruzamientos: ${formState.cruzamientos}
        Tiempo del Problema: ${formState.tiempoProblema}
        Motivo de la Queja: ${formState.motivoQueja}
    """.trimIndent()

    // Intento guardar el archivo
    return try {
        // Creo el archivo en la carpeta de descargas de la aplicación
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        FileOutputStream(file).use { fos ->
            fos.write(fileContent.toByteArray())
        }
        // Muestro una notificación de que se descargó el archivo
        showDownloadNotification(context, fileName)
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}