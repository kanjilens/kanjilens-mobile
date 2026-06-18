package com.example.kanjilens.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjilens.BuildConfig
import com.example.kanjilens.auth.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel(), modifier : Modifier = Modifier,  onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {

    var email by rememberSaveable {mutableStateOf("")}
    var password by rememberSaveable {mutableStateOf("")}

    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) {
            onLoginSuccess()
        }
    }
    Column(
        modifier = Modifier
                 .fillMaxSize()
                 .padding(16.dp),
            verticalArrangement = Arrangement.Center
    ){


        Text(
        text = "KanjiLens",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,

    )

        if (!BuildConfig.FIREBASE_ENABLED) {
            Text(
                text = "Modo local ativo: Firebase desabilitado para teste do OCR.",
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Button(onClick = onLoginSuccess) {
                Text("Entrar no teste OCR")
            }
            return@Column
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite seu email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Digite sua senha") }
        )
        Row() {
            Button(onClick = {
                viewModel.signIn(email, password)
            }) {
                Text("Entrar")
            }
            Button(onClick = {
                onGoToRegister()
            }) {
                Text("Registrar")
            }
        }
        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }



    }
}
