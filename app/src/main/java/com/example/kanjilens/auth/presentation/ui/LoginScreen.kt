package com.example.kanjilens.auth.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kanjilens.auth.presentation.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel(), modifier : Modifier = Modifier,  onLoginSuccess: () -> Unit) {

    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
                 .fillMaxSize()
                 .padding(16.dp),
            verticalArrangement = Arrangement.Center
    ){
        LaunchedEffect(viewModel.isLoggedIn) {
            if (viewModel.isLoggedIn) {
                onLoginSuccess()
            }
        }

        Text(
        text = "KanjiLens",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,

    )
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

        Button(onClick = {
            viewModel.signIn(email,password)
        }) {
            Text("Entrar")
        }
        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }



    }
}