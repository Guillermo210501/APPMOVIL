package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Define los colores personalizados fuera de cualquier contexto composable
val CustomBlue = Color(0xFF1C3258) // Azul personalizado para el fondo
val CustomTextFieldColor = Color(0xFF1E2126) // Color para los campos de texto
val ButtonTextColor = Color(0xFF000000) // Color del texto de los botones

// Definir esquemas de colores para tema claro y oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = CustomBlue,
    onPrimary = ButtonTextColor,
    // Colores específicos para texto en modo oscuro
    onBackground = Color.Black,  // Color principal del texto en modo oscuro
    onSurface = Color.Black,    // Color del texto en superficies (cards, etc)
    onSecondary = Color.LightGray // Color del texto secundario
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = CustomBlue,
    onPrimary = ButtonTextColor,
    // Colores específicos para texto en modo claro
    onBackground = Color.Black,  // Color principal del texto en modo claro
    onSurface = Color.Black, // Color del texto en superficies
    onSecondary = Color.Black    // Color del texto secundario
)
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomBlue)
            ) {
                content()
            }
        }
    )
}
