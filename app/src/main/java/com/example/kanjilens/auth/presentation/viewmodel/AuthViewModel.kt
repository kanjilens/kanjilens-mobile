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
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kanjilens.auth.data.local.SecureCredentialsStore
import com.example.kanjilens.auth.presentation.biometric.BiometricAuthManager

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val userRepository = UserRepository()

    private val credentialsStore =
        SecureCredentialsStore(getApplication())
    private val biometricAuthManager = BiometricAuthManager(getApplication())

    var isLoggedIn by mutableStateOf(repository.getCurrentUser() != null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<AuthError?>(null)
    var passwordResetSent by mutableStateOf(false)
    var showBiometricOptIn by mutableStateOf(false)
        private set

    val canUseBiometricLogin: Boolean
        get() = credentialsStore.hasCredentials() && biometricAuthManager.isBiometricAvailable()
    private var pendingEmail: String? = null
    private var pendingPassword: String? = null



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
                offerBiometricOptInIfNeeded(email, password)
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
                    userRepository.addCurrentUser(uid = currentUser.uid, name = name, email = email)
                }
                isLoggedIn = true
                isLoading = false
                Log.d("AuthViewModel", "Conta criada")
                offerBiometricOptInIfNeeded(email, password)
            },
            onError = { erro ->
                errorMessage = erro
                isLoading = false
            }
        )
    }
    private fun offerBiometricOptInIfNeeded(email: String, password: String) {
        val biometricAvailable = biometricAuthManager.isBiometricAvailable()
        val alreadyConfigured = credentialsStore.hasCredentials()
        val declined = credentialsStore.isBiometricDeclined()

        if (biometricAvailable && !alreadyConfigured && !declined) {
            pendingEmail = email
            pendingPassword = password
            showBiometricOptIn = true
        }
    }
    fun confirmBiometricOptIn() {
        val email = pendingEmail
        val password = pendingPassword
        if (email != null && password != null) {
            credentialsStore.saveCredentials(email, password)
        }
        clearBiometricOptInState()
    }

    fun declineBiometricOptIn() {
        credentialsStore.setBiometricDeclined(true)
        clearBiometricOptInState()
    }

    private fun clearBiometricOptInState() {
        showBiometricOptIn = false
        pendingEmail = null
        pendingPassword = null
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
    fun signInWithBiometrics() {

        val credentials = credentialsStore.getCredentials()

        if (credentials == null) {
            errorMessage = AuthError.InvalidCredentials
            return
        }

        isLoading = true
        errorMessage = null

        repository.signIn(
            email = credentials.email,
            password = credentials.password,
            onSuccess = {
                isLoggedIn = true
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
}
