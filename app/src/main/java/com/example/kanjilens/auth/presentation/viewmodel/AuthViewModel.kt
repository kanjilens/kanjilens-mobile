package com.example.kanjilens.auth.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kanjilens.auth.data.repository.AuthRepository
import com.example.kanjilens.auth.data.repository.UserRepository
import com.example.kanjilens.auth.domain.AuthError
import com.example.kanjilens.auth.domain.PasswordValidator

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val userRepository = UserRepository()

    var isLoggedIn by mutableStateOf(repository.getCurrentUser() != null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<AuthError?>(null)
    var passwordResetSent by mutableStateOf(false)

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

    fun signOut(){
        repository.signOut()
        isLoggedIn=false
        isLoading = false
        errorMessage = null
    }

    fun createAccount(name: String, email: String, password: String) {
        errorMessage = null

        val passwordError = PasswordValidator.validate(password)

        if (passwordError != null) {
            errorMessage = passwordError
            isLoading = false
            return
        }

        isLoading = true

        repository.createAccount(
            name = name,
            email = email,
            password = password,
            onSuccess = {
                val currentUser = repository.getCurrentUser()

                if (currentUser != null) {
                    userRepository.addCurrentUser(
                        uid = currentUser.uid,
                        name = name,
                        email = email
                    )
                }
                isLoggedIn = true
                isLoading = false
                Log.d("AuthViewModel", "Conta criada")
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
    fun sendPasswordResetEmail(email: String) {
        errorMessage = null
        passwordResetSent = false

        if (email.isBlank()) {
            errorMessage = AuthError.MissingEmail
            return
        }

        isLoading = true

        repository.sendPasswordResetEmail(
            email = email,
            onSuccess = {
                passwordResetSent = true
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
    fun clearPasswordResetState() {
        passwordResetSent = false
    }
}
