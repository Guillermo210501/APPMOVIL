package com.example.myapplication

import AdminViewQuejasScreen
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.screens.Admin.AdminScreen
import AdminViewQuejasScreen
import com.example.myapplication.ui.theme.screens.Admin.UsuariosAdminScreen
import com.example.myapplication.ui.theme.screens.Quejas.MainAnonimaScreen
import com.example.myapplication.ui.theme.screens.Quejas.QuejasAnonimasScreen
import com.example.myapplication.ui.theme.screens.Usuario.CreateAccountScreen
import com.example.myapplication.ui.theme.screens.Usuario.InicioScreen
import com.example.myapplication.ui.theme.screens.Usuario.LoginScreen
import com.example.myapplication.ui.theme.screens.Usuario.MainScreen
import com.example.myapplication.ui.theme.viewmodel.QuejaViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.ui.theme.screens.Admin.MainAdminScreen
import com.example.myapplication.ui.theme.screens.Anonimo.SeguimientoQuejasAnonimas
import com.example.myapplication.ui.theme.screens.Api.QuienesSomosScreen
import com.example.myapplication.ui.theme.screens.Quejas.QuejaScreen
import com.example.myapplication.ui.theme.screens.Usuario.SeguimientoQuejasUserScreen
import com.example.myapplication.ui.theme.screens.admin.QuejasAdminScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Definición centralizada de las rutas usando sealed class
    sealed class Routes(val route: String) {
        object Inicio : Routes("inicio")
        object Login : Routes("login")
        object Main : Routes("main")
        object MainAdmin : Routes("main_admin")
        object MainAnonima : Routes("main_anonima")
        object QuejasAnonimas : Routes("quejas_anonimas/{tipo}")
        object Quejas : Routes("quejas/{tipo}")
        object AdminVerQuejas : Routes("admin_ver_quejas/{servicio}")
        object CrearCuenta : Routes("crear_cuenta")
        object Admin : Routes("admin")
        object QuejasAdmin : Routes("quejas_admin")
        object GestionarUsuarios : Routes("gestionar_usuarios")
        object QuienesSomos : Routes("quienes_somos")
        object SeguimientoQuejas : Routes("seguimiento_quejas")
        object SeguimientoQuejasAnonimas : Routes("seguimiento_quejas_anonimas")
    }

    // Inyectar AppDatabase usando Hilt
    @Inject lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance() // Inicializa Firebase Auth

        // Solicitar permisos de almacenamiento para versiones <= 28
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            requestStoragePermission()
        }

        // Solicitar permiso de notificaciones en Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        // Configuración de Compose
        setContent {
            val navController = rememberNavController() // Controlador de navegación

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationContent(navController, auth)
                }
            }
        }
    }

    @Composable
    fun AppNavigationContent(navController: androidx.navigation.NavHostController, auth: FirebaseAuth) {
        val quejaViewModel: QuejaViewModel = hiltViewModel()

        NavHost(
            navController = navController,
            startDestination = Routes.Inicio.route // Usamos el sealed class aquí
        ) {
            composable(Routes.Inicio.route) { InicioScreen(navController, auth) }
            composable(Routes.Login.route) { LoginScreen(navController, auth) }
            composable(Routes.Main.route) { MainScreen(navController, auth) }
            composable(Routes.MainAnonima.route) { MainAnonimaScreen(navController) }
            composable(Routes.QuienesSomos.route) {
                QuienesSomosScreen(navController = navController)
            }
            composable(Routes.SeguimientoQuejas.route) {
                SeguimientoQuejasUserScreen(navController = navController, auth = auth)
            }

            composable(
                route = Routes.QuejasAnonimas.route,
                arguments = listOf(navArgument("tipo") { type = NavType.StringType })
            ) { backStackEntry ->
                val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
                QuejasAnonimasScreen(tipo = tipo, navController = navController)
            }
            composable(
                route = Routes.AdminVerQuejas.route,
                arguments = listOf(navArgument("servicio") { type = NavType.StringType })
            ) { backStackEntry ->
                val servicio = backStackEntry.arguments?.getString("servicio") ?: ""
                AdminViewQuejasScreen(navController = navController, servicio = servicio)
            }
            composable(Routes.CrearCuenta.route) { CreateAccountScreen(navController, auth) }
            composable(Routes.MainAdmin.route) {
                MainAdminScreen(navController = navController, auth = auth)
            }
            composable(Routes.Admin.route) { AdminScreen(navController = navController) }
            composable(Routes.QuejasAdmin.route) { QuejasAdminScreen(navController = navController) }
            composable(Routes.GestionarUsuarios.route) { UsuariosAdminScreen(navController = navController) }

            composable(Routes.SeguimientoQuejasAnonimas.route) {
                SeguimientoQuejasAnonimas(navController = navController)
            }

            composable(
                route = Routes.Quejas.route,
                arguments = listOf(navArgument("tipo") { type = NavType.StringType })
            ) { backStackEntry ->
                val tipo = backStackEntry.arguments?.getString("tipo") ?: ""
                QuejaScreen(tipo = tipo, navController = navController)
            }
        }
    }

    // Solicitar permiso para almacenamiento en versiones <= 28
    private fun requestStoragePermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                println("Permiso de almacenamiento denegado por el usuario")
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    // Solicitar permiso de notificaciones en versiones >= 33
    private fun requestNotificationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                println("Permiso de notificaciones denegado por el usuario")
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}