package com.example.kanjilens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kanjilens.auth.presentation.ui.LoginScreen
import com.example.kanjilens.auth.presentation.ui.RegisterScreen
import com.example.kanjilens.kanji.presentation.ui.CameraScreen
import com.example.kanjilens.kanji.presentation.ui.HomeScreen
import com.example.kanjilens.settings.data.local.AppSettings
import com.example.kanjilens.settings.data.local.AppSettingsStore
import com.example.kanjilens.settings.presentation.ui.SettingsScreen
import com.example.kanjilens.ui.theme.KanjiLensTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect

import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()



        setContent {

            val settings by AppSettingsStore.settingsFlow(this)
                .collectAsState(initial = AppSettings())

            LaunchedEffect(settings.language) {
                if (settings.language.isNotBlank()) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(settings.language)
                    )
                }
            }
            val context = LocalContext.current


                KanjiLensTheme(darkTheme = settings.darkMode) {
                    val navController = rememberNavController()
                    val startDestination =
                        if (FirebaseAuth.getInstance().currentUser != null) "Home" else "Login"


                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("Login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("Home") {
                                        popUpTo("Login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable("Register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("Home") {
                                        popUpTo("Login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(route = "Home") {
                            HomeScreen(
                                onOpenCamera = {
                                    navController.navigate("Camera") { launchSingleTop = true }
                                },
                                onOpenSettings = {
                                    navController.navigate("Settings") {
                                        popUpTo("Home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onLogout = {
                                    navController.navigate("Login") {
                                        popUpTo("Home") { inclusive = true }
                                        launchSingleTop = true
                                    }
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
                        composable(route = "Settings") {
                            SettingsScreen(
                                onOpenHome = {
                                    navController.navigate("Home") {
                                        popUpTo("Home") { inclusive = false }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onOpenCamera = {
                                    navController.navigate("Camera") { launchSingleTop = true }
                                },
                                onLogout = {
                                    navController.navigate("Login") {
                                        popUpTo("Home") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }


