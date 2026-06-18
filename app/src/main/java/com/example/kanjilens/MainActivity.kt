package com.example.kanjilens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kanjilens.auth.presentation.ui.LoginScreen
import com.example.kanjilens.auth.presentation.ui.RegisterScreen
import com.example.kanjilens.kanji.presentation.ui.CameraScreen
import com.example.kanjilens.kanji.presentation.ui.HomeScreen
import com.example.kanjilens.ui.theme.KanjiLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanjiLensTheme {
                val navController = rememberNavController()
                val startDestination = if (BuildConfig.FIREBASE_ENABLED) "Login" else "Home"
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("Login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("Home"){
                                    popUpTo("Login") { inclusive = true }
                                }
                            },
                            onGoToRegister = {
                                navController.navigate("Register")
                            }
                        )
                    }
                    composable("Register"){
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("Home"){
                                    popUpTo("Login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(route = "Home") {
                        HomeScreen(
                            onOpenCamera = {
                                navController.navigate("Camera")
                            },
                            onLogout ={
                                navController.navigate(route="Login")
                            }
                        )
                    }
                    composable(route = "Camera") {
                        CameraScreen(
                            onClose = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
