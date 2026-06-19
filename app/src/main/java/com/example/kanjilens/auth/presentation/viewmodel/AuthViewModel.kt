package com.example.kanjilens.auth.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kanjilens.auth.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var isLoggedIn by mutableStateOf(repository.getCurrentUser() != null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun signIn(email: String, password: String) {
        isLoading = true
        errorMessage = null
        repository.signIn(
            email = email,
            password = password,
            onSuccess = {
                Log.d("AuthViewModel", "Login ok")
                isLoggedIn = true
                isLoading = false
            },
            onError = { erro ->
                Log.w("AuthViewModel", "Erro: $erro")
                errorMessage = erro
                isLoading = false
            }
        )
    }

    fun signout(){
        repository.signout()
        isLoggedIn=false
        isLoading = false
        errorMessage = null
    }

    fun createAccount(name: String, email: String, password: String) {
        isLoading = true
        errorMessage = null
        repository.createAccount(
            name = name,
            email = email,
            password = password,
            onSuccess = {
                isLoggedIn = true
                isLoading = false
            },
            onError = { erro ->
                errorMessage = erro
                isLoading = false
            }
        )
    }

    fun clearError() {
        errorMessage = null
    }
}
