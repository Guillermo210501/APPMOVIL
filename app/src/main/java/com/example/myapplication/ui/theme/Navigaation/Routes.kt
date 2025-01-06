package com.example.myapplication.navigation

import AdminViewQuejasScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.screens.Admin.*
import com.example.myapplication.ui.theme.screens.Anonimo.SeguimientoQuejasAnonimas
import com.example.myapplication.ui.theme.screens.Api.QuienesSomosScreen
import com.example.myapplication.ui.theme.screens.Quejas.*
import com.example.myapplication.ui.theme.screens.Usuario.*
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.ui.theme.screens.Quejas.MainAnonimaScreen
import com.example.myapplication.ui.theme.screens.admin.QuejasAdminScreen

// Este objeto contiene todas las rutas de navegación de mi app
// Lo uso para evitar errores de escritura en las rutas y tenerlas centralizadas
object NavigationRoutes {
    const val INICIO = "inicio"
    const val LOGIN = "login"
    const val MAIN = "main"
    const val MAIN_ANONIMA = "main_anonima"
    // Esta ruta tiene un parámetro 'tipo' que uso para saber qué tipo de queja es
    const val QUEJAS_ANONIMAS = "quejas_anonimas/{tipo}"
    const val QUEJAS = "quejas/{tipo}"
    const val CREAR_CUENTA = "crear_cuenta"
    const val ADMIN = "admin"
    const val MAIN_ADMIN = "main_admin"
    const val QUEJAS_ADMIN = "quejas_admin"
    // Esta ruta tiene un parámetro 'servicio' para filtrar quejas por servicio
    const val ADMIN_VER_QUEJAS = "admin_ver_quejas/{servicio}"
    const val GESTIONAR_USUARIOS = "gestionar_usuarios"
    const val QUIENES_SOMOS = "quienes_somos"
    const val SEGUIMIENTO_QUEJAS = "seguimiento_quejas"
    const val SEGUIMIENTO_QUEJAS_ANONIMAS = "seguimiento_quejas_anonimas"
}

// Esta es mi función principal de navegación
// Aquí defino todas las pantallas y cómo navegar entre ellas
@Composable
fun AppNavigation(navHostController: NavHostController, auth: FirebaseAuth) {
    NavHost(
        navController = navHostController,
        // La app siempre empieza en la pantalla de inicio
        startDestination = NavigationRoutes.INICIO
    ) {
        // Pantallas principales de la app
        // Cada composable representa una pantalla diferente
        composable(NavigationRoutes.INICIO) {
            InicioScreen(navHostController, auth)
        }

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(navHostController, auth)
        }

        composable(NavigationRoutes.MAIN) {
            MainScreen(navHostController, auth)
        }

        // Esta sección es para usuarios anónimos
        // Pueden hacer quejas sin necesidad de registrarse
        composable(NavigationRoutes.MAIN_ANONIMA) {
            MainAnonimaScreen(navHostController)
        }

        // Aquí configuro la navegación para quejas anónimas
        // Uso argumentos para saber qué tipo de queja quieren hacer
        composable(
            route = NavigationRoutes.QUEJAS_ANONIMAS,
            arguments = listOf(navArgument("tipo") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
            QuejasAnonimasScreen(tipo = tipo, navController = navHostController)
        }

        // Esta sección es para usuarios registrados
        // Pueden hacer quejas con su cuenta
        composable(
            route = NavigationRoutes.QUEJAS,
            arguments = listOf(navArgument("tipo") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
            QuejaScreen(tipo = tipo, navController = navHostController)
        }

        // Pantallas relacionadas con la cuenta de usuario
        composable(NavigationRoutes.CREAR_CUENTA) {
            CreateAccountScreen(navHostController, auth)
        }

        composable(NavigationRoutes.SEGUIMIENTO_QUEJAS) {
            SeguimientoQuejasUserScreen(navController = navHostController, auth = auth)
        }

        // Esta sección es solo para administradores
        // Aquí pueden gestionar quejas y usuarios
        composable(NavigationRoutes.ADMIN) {
            AdminScreen(navController = navHostController)
        }

        composable(NavigationRoutes.MAIN_ADMIN) {
            MainAdminScreen(navController = navHostController, auth = auth)
        }

        composable(NavigationRoutes.QUEJAS_ADMIN) {
            QuejasAdminScreen(navController = navHostController)
        }

        composable(NavigationRoutes.GESTIONAR_USUARIOS) {
            UsuariosAdminScreen(navController = navHostController)
        }

        composable(NavigationRoutes.SEGUIMIENTO_QUEJAS_ANONIMAS) {
            SeguimientoQuejasAnonimas(navController = navHostController)
        }

        // Esta ruta es para que los admins vean quejas por servicio
        composable(
            route = NavigationRoutes.ADMIN_VER_QUEJAS,
            arguments = listOf(navArgument("servicio") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val servicio = backStackEntry.arguments?.getString("servicio") ?: ""
            AdminViewQuejasScreen(navController = navHostController, servicio = servicio)
        }

        // Pantalla informativa sobre la app
        composable(NavigationRoutes.QUIENES_SOMOS) {
            QuienesSomosScreen(navController = navHostController)
        }
    }
}