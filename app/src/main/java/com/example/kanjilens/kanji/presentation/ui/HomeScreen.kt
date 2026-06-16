package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjilens.auth.presentation.viewmodel.AuthViewModel

@Composable
fun HomeScreen(viewModel: AuthViewModel = viewModel(), onLogout: () -> Unit) {

    Column() {

        LaunchedEffect(viewModel.isLoggedIn) {
            if (!viewModel.isLoggedIn) onLogout()
        }

    Text(text="Hello World")
        Button(onClick = {
           viewModel.signout()

        }) {
            Text("Sair da conta")
        }
    }
}